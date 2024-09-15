package adminApi.com.datareader.data.containers

import adminApi.com.common.dataflow.*
import adminApi.com.common.statistics.*
import adminApi.com.datareader.data.DataProvider
import adminApi.com.datareader.data.DataProvider.MeterCompanion
import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.general.models.data.ICommonData
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.coroutines.CoroutineContext
import kotlin.math.E

abstract class DataContainer<T:DataModelClass>(): DataFlowMeter {

    companion object MeterCompanion : DataFlowMeterCompanion {
        override var parent: Any? = null
        override var unitName: String = "DataContainer"
        override var module: ModuleName = ModuleName.DATAREADER
        override var supplierName: String = ""
        override val measurements: MutableList<Measurement> = mutableListOf()

        override val onDispatcherResultsSubmitted: ((FlowPoint)-> Unit) = {
           this.registerMeasurement(it)
        }
        override val onMeterResultsSubmitted: ((Measurement) -> Unit)? = {
                this.updateMeasurement(it)
        }

        override fun updateMeasurement(measurement: Measurement) {
            this.measurements.firstOrNull { it.methodName == measurement.methodName }?.also {
                it.elapsedTime = measurement.elapsedTime
                it.flowType = measurement.flowType
                it.success = measurement.success
            }
        }

        override fun registerMeasurement(data : FlowPoint) {
            val measurement = Measurement()
            measurement.also {
                it.module = data.module
                it.supplierName = data.supplierName?:""
                it.methodName = data.targetMethodName?: ""
            }
            this.measurements.add(measurement)
        }

        fun getLastMeasurement(): MeasurableCallResult? {
            val last = DataProvider.MeterCompanion.measurements.last()
            return last
        }
    }

    private val dataDispatcher: DataDispatcher = DataDispatcher()

    abstract var supplierId:Int
    abstract var dataCategory : DataCategory
    private var busy = false
        set(value) {
            if(field != value) {
                field = value
                dataDispatcher.setRecipientBusyStatus(field)
            }

        }
    private var completeDataUploadFromSupplier = false
    private var initialUpdate = false
    var mapping: HashMap<String,String>? = null
    private val jsonItems : MutableList<JsonElement> = mutableListOf()
    private val records : MutableList<T> = mutableListOf()
    private var toSaveBuffer : MutableList<T> = mutableListOf()
    var onDataForUpdated: ((List<T>)->Unit)? = null
    var onDataForRemoval : ((List<T>)->Unit)? = null

    init {
        initCompanion()
    }

    abstract fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? = null) : T
    abstract suspend fun supplyData(dataItems : List<ICommonData>):Boolean

    var companion: MeterCompanion? = null
    override fun initCompanion() {
        companion = MeterCompanion
        if(companion != null){
            companion!!.init("DataContainer", ModuleName.DATAREADER, "action", this)
            dataDispatcher.subscribeToCallResults(onDispatcherResultsSubmitted)
        }
    }

    private fun sendRedundantForRemoval(){
        if(this.completeDataUploadFromSupplier && !this.initialUpdate){
            val redundantList = this.records.filter { it.fromDataManager && !it.markedForRemoval }
            if(redundantList.isNotEmpty()){
                onDataForRemoval?.invoke(redundantList)
            }
        }
    }

    private fun sendForUpdate(){
        if(onDataForUpdated!=null){
            if(this.toSaveBuffer.isNotEmpty()){
                onDataForUpdated?.invoke(this.toSaveBuffer)
                this.toSaveBuffer = mutableListOf()
            }
        }
    }

    private fun getExistentIfDataDiffer(record: T):T?{
        val index = this.records.indexOfFirst {it.providerId == record.providerId }
        val newRecordData = record.toData()
        newRecordData.id = 0
        val foundRecordData = this.records[index].toData()
        foundRecordData.id = 0
        return if(newRecordData == foundRecordData) {
            null
        }else{
            this.records[index]
        }
    }

    private fun replaceRecord(oldRecord: T, newRecord: T){
        val index = this.records.indexOfFirst {it.providerId == oldRecord.providerId }
        if(index >= 0){
            this.records[index] = newRecord
        }
    }

    private fun beforeUpdate(){
        busy = true
    }

    private fun afterUpdate(){
        sendForUpdate()
        sendRedundantForRemoval()
        this.initialUpdate = false
        busy = false
    }

    private fun addRecord(record: T) {
        if(this.initialUpdate){
            this.records.add(record)
            return
        }
        val index = this.records.indexOfFirst { it.providerId == record.providerId }
        if (index < 0) {
            this.records.add(record)
            if (!record.fromDataManager) {
                this.toSaveBuffer.add(record)
            }
        } else {
            val existent = getExistentIfDataDiffer(record)
            if (existent != null) {
                if (existent.fromDataManager) {
                    record.id = existent.id
                    this.toSaveBuffer.add(record)
                    this.replaceRecord(existent, record)
                } else {
                    this.toSaveBuffer.add(existent)
                }
            } else {
                this.records[index].fromDataManager = false
            }
        }
    }

    suspend fun setDataFromSource(container: DataFlowContainer) = dataDispatcher.receiveData<T>(
        container,
        RouteHop(ModuleName.DATAREADER,supplierName, dataCategory,"setDataFromSource"),
        this.busy,
        {
        it.onBuffered ={

        }

        it.onRelease = {
            beforeUpdate()
            it.dataList.forEach{ addRecord(it as T) }
            afterUpdate()
        }
        }) {
            val dataItems = startMeasure<List<T>>(container.lastKnownTargetMethod() , FlowType.UPDATE, onMeterResultsSubmitted) {
                val jsonList: List<JsonElement> = container.dataList.filterIsInstance<JsonElement>()
                jsonList.map { createDataItem(it, mapping) }
            }

            setUpdateResult<T>(dataItems,getLastMeasurement())
        }

    suspend fun setData(data : List<T>, fromDataManager : Boolean = true ):Boolean{

        var attemps = 20
        while (busy){
            delay(5000)
            attemps--
            if (attemps == 0){
                return false
            }
        }
        beforeUpdate()
        data.forEach {
            it.fromDataManager = fromDataManager
            this.addRecord(it)
        }
        afterUpdate()
        return true
    }

    fun getRecords(): List<T>{
        return this.records
   }

}
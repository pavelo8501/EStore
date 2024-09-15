package po.estoreAdminApi.common.dataflow

import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonElement
import org.koin.core.definition.Callbacks
import po.estoreAdminApi.common.statistics.CallResult
import po.estoreAdminApi.common.statistics.DataCategory
import po.estoreAdminApi.common.statistics.DataFlowContainer
import po.estoreAdminApi.common.statistics.DataSetType
import po.estoreAdminApi.common.statistics.DataType
import po.estoreAdminApi.common.statistics.FlowPoint
import po.estoreAdminApi.common.statistics.FlowType
import po.estoreAdminApi.common.statistics.MeasurableCallResult
import po.estoreAdminApi.common.statistics.Measurement
import po.estoreAdminApi.common.statistics.ModuleName
import po.estoreAdminApi.common.statistics.RouteHop
import po.estoreAdminApi.common.statistics.ServiceCallResult
import po.estoreAdminApi.datareader.classes.DataServiceCallResult
import javax.security.auth.callback.Callback




//interface DataDispatcher {
//    val dataBuffer: MutableList<DataBufferContainer>
//    var onResultsSubmit : ((ServiceCallResult) -> Unit)?
//    var onDataReceived: (DispatcherHopData) -> Unit
//    var onDataBeforeDispatch: (() -> Unit)?
//    var onException: ((Exception) -> Unit)?
//    var onDataAfterDispatch: ((CallResult) -> Unit)?
//    var onDataDispatchComplete: (() -> Unit)?
//    var onBuffered: (()->Unit)?
//    //var onRelease: (()->List<ICommonData>)?
//
//    abstract fun init(onSubmitResult: ((ServiceCallResult) -> Unit)?)
//    fun <T>addToBuffer(dataItems: List<T>,container : DataFlowContainer)
////    abstract suspend fun <T>receiveData(
////        dataContainer: DataFlowContainerImpl,
////        busy: Boolean,
////        body: (DataDispatcherImpl.() -> Unit)?,
////        receiver: DataDispatcherImpl.() -> List<T>
////    )
//    abstract suspend fun getData(
//        dataCategory: DataCategory,
//        body: ((DataDispatcherImpl) -> Unit)? = null,
//        receiver: suspend DataDispatcherImpl.() -> Unit
//    ): DataDispatcher
//    abstract fun processTransferResult(callResult: DataServiceCallResult): ServiceCallResult
//}

interface BufferRecordContext {
    var measurement: Measurement

    fun extractContainer(): DataFlowContainer
}

data class BufferItem(
    private val dataContainer: DataFlowContainer
):BufferRecordContext{

    override var measurement: Measurement = Measurement()
    init {
        measurement.count = dataContainer.dataList.size
        measurement.flowType = FlowType.BUFFERED
        measurement.methodName = dataContainer.lastKnownTargetMethod()
        measurement.supplierName = dataContainer.lastHop()?.supplierName?:""
        measurement.dataCategory = dataContainer.lastHop()?.dataCategory?: DataCategory.PRODUCER
    }

    override fun extractContainer(): DataFlowContainer {
        measurement.stop()
        dataContainer.addMeasurement(measurement)
        return dataContainer
    }
}

class DataDispatcher() :  DataFlowMeasurable {
 companion object DataDispatcherCompanion{
     fun createDispatchContainer(sender: ModuleName, targetMethodName: String):DataFlowContainer{
         val dataFlowContainer =  DataFlowContainer()
         dataFlowContainer.addRouteInformation(RouteHop(sender,targetMethodName))
         return dataFlowContainer
     }
 }


    val dataBuffer: MutableList<BufferItem> = mutableListOf()
    var recipientBusy: Boolean = false
        set(value) {
            if (field != value) {
                field = value
            }
        }

    var currentContainer: DataFlowContainer? = null


    var onResultsSubmit: ((MeasurableCallResult) -> Unit)? = null
    var onBeforeDispatch: ((FlowPoint) -> Unit)? = null
    var onDataAfterDispatch: ((CallResult) -> Unit)? = null
    var onDataDispatchComplete: (() -> Unit)? = null
    var onException: ((Exception) -> Unit)? = null
    var onBuffered: (() -> Unit)? = null

    var onRelease: ((DataFlowContainer)->Unit)? = null

    fun subscribeToCallResults(fn: ((FlowPoint) -> Unit)){
        onBeforeDispatch = fn
    }

    fun releaseFromBuffer(){
        if(dataBuffer.isNotEmpty()){
            val dataBufferItem = dataBuffer.removeAt(dataBuffer.size - 1)
            val container = dataBufferItem.extractContainer()
            onRelease?.invoke(container)
        }
    }

    fun setRecipientBusyStatus(value: Boolean) {
        recipientBusy = value
        if(!value){
            releaseFromBuffer()
        }
    }

    fun <T> addToBuffer(container: DataFlowContainer) {
       // container.updateData<T>(dataItems,DataSetType.COMPLETE,DataType.DATARECORD)
        val bufferItem = BufferItem(container)
        dataBuffer.add(bufferItem)
        onBuffered?.invoke()
    }

    suspend fun <T>receiveData(
        container: DataFlowContainer,
        routeHop : RouteHop,
        busy: Boolean,
        body: ((DataDispatcher) -> Unit)?,
        receiver: DataDispatcher.() -> Unit
    ){
        currentContainer = container
        currentContainer!!.addRouteInformation(routeHop)
        body?.invoke(this)
        onBeforeDispatch?.invoke(routeHop)
        try {
            this.recipientBusy = busy
            receiver.invoke(this)
        }catch (e: Exception){
            onException?.invoke(e)
        }
    }

    suspend fun getData(
        container: DataFlowContainer,
        routeHop : RouteHop,
        destinationPeer:  ( suspend (DataFlowContainer) -> Unit)? = null,
        receiver: suspend DataDispatcher.() -> Unit
    ) : DataFlowContainer {
        currentContainer = container
        currentContainer!!.addRouteInformation(routeHop)
        onBeforeDispatch?.invoke(routeHop)
        try{
            receiver.invoke(this)
        } catch (e: Exception) {
            onException?.invoke(e)
        }

        if(currentContainer!= null){
            destinationPeer?.invoke(currentContainer!!)
            onDataDispatchComplete?.invoke()
        }
        return currentContainer!!
    }

    fun <T>setUpdateResult(data: List<T>, measurement: MeasurableCallResult? = null){
        if(currentContainer!= null){
            if(measurement != null){
                currentContainer!!.addMeasurement(measurement)
            }
            currentContainer!!.updateData(data, DataSetType.COMPLETE, DataType.DATARECORD)
        }
        if(recipientBusy){
            addToBuffer<T>(currentContainer!!)
        }else{
            onRelease?.invoke(currentContainer!!)
        }
    }

    fun setCallResult(callResult: DataServiceCallResult,measurement: MeasurableCallResult? = null): ServiceCallResult {
        if(currentContainer!= null){
            if(measurement != null){
                currentContainer!!.addMeasurement(measurement)
            }
            currentContainer!!.importData(callResult.dataContainer)
        }
        return callResult
    }



//    fun createContainer(container: DataFlowContainer,route: DispatcherHopData, measurement: IMeasurableCallResult?): DataFlowContainer {
//        container.addRouteInformation(route)
//        if (measurement != null) {
//            container.addMeasurement(measurement)
//        } else {
//            val a = 10
//        }
//        return container
//    }


}

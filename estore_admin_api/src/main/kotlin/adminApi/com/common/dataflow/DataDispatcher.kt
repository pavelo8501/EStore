package adminApi.com.common.dataflow

import adminApi.com.common.statistics.*
import adminApi.com.datareader.classes.DataServiceCallResult
import adminApi.com.datareader.data.containers.DataContainer
import adminApi.com.general.models.data.ICommonData
import kotlinx.coroutines.*
import kotlinx.serialization.json.JsonElement
import org.koin.core.definition.Callbacks
import javax.security.auth.callback.Callback


interface DataDispatcherRecord {
    val sender: String
    val recipient: String
    val count: Int
}

interface DataBufferContainer {
    val timeStart:Long
    val dataContainer :DataFlowContainer
}

interface DataDispatcher {
    val dataBuffer: MutableList<DataBufferContainer>
    var onResultsSubmit : ((ServiceCallResult) -> Unit)?
    var onDataReceived: (DataDispatcherRecord) -> Unit
    var onDataBeforeDispatch: (() -> Unit)?
    var onException: ((Exception) -> Unit)?
    var onDataAfterDispatch: ((CallResult) -> Unit)?
    var onDataDispatchComplete: (() -> Unit)?
    var onBuffered: (()->Unit)?
    //var onRelease: (()->List<ICommonData>)?

    abstract fun init(onSubmitResult: ((ServiceCallResult) -> Unit)?)
    fun <T>addToBuffer(dataItems: List<T>,container : DataFlowContainerImpl)
//    abstract suspend fun <T>receiveData(
//        dataContainer: DataFlowContainerImpl,
//        busy: Boolean,
//        body: (DataDispatcherImpl.() -> Unit)?,
//        receiver: DataDispatcherImpl.() -> List<T>
//    )
    abstract suspend fun getData(
        dataCategory: DataCategory,
        body: ((DataDispatcherImpl) -> Unit)? = null,
        receiver: suspend DataDispatcherImpl.() -> Unit
    ): DataDispatcher
    abstract fun processTransferResult(callResult: DataServiceCallResult): ServiceCallResult
}

data class DataBufferContainerImpl(
    override val timeStart: Long,
    override val dataContainer: DataFlowContainer,
):DataBufferContainer{}

data class DataDispatcherMarker(
    override val sender: String,
    override val recipient: String,
    override val count: Int
) : DataDispatcherRecord

class DataDispatcherImpl() : DataDispatcher, DataFlowMeasurable {
    override val dataBuffer: MutableList<DataBufferContainer> = mutableListOf()
    var recipientBusy: Boolean = false
    override var onResultsSubmit: ((ServiceCallResult) -> Unit)? = null
    override var onDataReceived: (DataDispatcherRecord) -> Unit = {}
    override var onDataBeforeDispatch: (() -> Unit)? = null
    override var onDataAfterDispatch: ((CallResult) -> Unit)? = null
    override var onDataDispatchComplete: (() -> Unit)? = null
    override var onException: ((Exception) -> Unit)? = null
    override var onBuffered: (() -> Unit)? = null

    var onRelease: ((List<ICommonData>)->Unit)? = null

    override fun init(onSubmitResult: ((ServiceCallResult) -> Unit)?) {
        onResultsSubmit = onSubmitResult
    }

    fun releaseFromBuffer(){
        if(dataBuffer.isNotEmpty()){
            val dataBufferItem = dataBuffer.removeAt(dataBuffer.size - 1)
            val list = dataBufferItem.dataContainer.dataList as List<ICommonData>
            onRelease?.invoke(list)
        }
    }

    fun setRecipientBusyStatus(busy: Boolean) {
        if(recipientBusy && !busy){
            releaseFromBuffer()
        }
    }

    override fun <T> addToBuffer(dataItems: List<T>, container: DataFlowContainerImpl) {
        container.updateData(dataItems,DataSetType.COMPLETE,DataType.DATARECORD)
        val bufferItem = DataBufferContainerImpl(System.currentTimeMillis(), container)
        dataBuffer.add(bufferItem)
    }

    suspend fun <T> receiveData(
        dataContainer: DataFlowContainerImpl,
        busy: Boolean,
        body: ((DataDispatcherImpl) -> Unit)?,
        receiver: DataDispatcherImpl.() -> List<T>
    ) {
        this.recipientBusy = busy
        val itemList = receiver(this)
        if (busy) {
            addToBuffer(itemList,dataContainer)
            if (body != null) {
                onBuffered?.invoke()
            }
        } else {
            if (body != null) {
                onRelease?.invoke(itemList as List<ICommonData>)
            }
        }
    }

    override suspend fun getData(
        dataCategory: DataCategory,
        body: (DataDispatcherImpl.() -> Unit)?,
        receiver: suspend DataDispatcherImpl.() -> Unit
    ): DataDispatcher {
        if (body != null) {
            this.body()
        }
        try{
            onDataBeforeDispatch?.invoke()
            onDataDispatchComplete?.invoke()
            val bodyResult = this.receiver()
            if (onDataAfterDispatch != null) {
                val result = CallResultImpl(listOf(bodyResult))
                onDataAfterDispatch!!.invoke(result)
            }
            onDataDispatchComplete?.invoke()
        } catch (e: Exception) {
            onException?.invoke(e)
        }
        return this
    }

    override fun processTransferResult(callResult: DataServiceCallResult): ServiceCallResult {
        val result: ServiceCallResult = callResult
        if (onResultsSubmit != null){
            onResultsSubmit?.invoke(result)
        }
        return result
    }

    fun startJourney(container: DataFlowContainerImpl,route: DataDispatcherRecord, measurement: IMeasurableCallResult?): DataFlowContainerImpl {
        container.addRouteInformation(route)
        if (measurement != null) {
            container.addMeasurement(measurement)
        } else {
            val a = 10
        }
        return container
    }
}

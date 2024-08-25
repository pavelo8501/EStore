package adminApi.com.common.dataflow

import adminApi.com.common.statistics.CallResult
import adminApi.com.common.statistics.CallResultImpl
import adminApi.com.common.statistics.DataCategory
import adminApi.com.common.statistics.ServiceCallResult
import adminApi.com.datareader.classes.DataServiceCallResult
import adminApi.com.general.models.data.ICommonData


interface DataDispatcherResult {
    val sender: String
    val recipient: String
    val count: Int
}

interface DataDispatcher {
    var onResultsSubmit : ((ServiceCallResult) -> Unit)?
    var onDataReceived: (DataDispatcherResult) -> Unit
    var onDataBeforeDispatch: (() -> Unit)?
    var onException: ((Exception) -> Unit)?
    var onDataAfterDispatch: ((CallResult) -> Unit)?
    var onDataDispatchComplete: (() -> Unit)?
    var onBuffered: (()->Unit)?
    var onBufferRelease: (()->Unit)?

    abstract fun init(onSubmitResult: ((ServiceCallResult) -> Unit)?)
    fun <L> addToBuffer(dataItems: List<L>)
    abstract suspend fun <L,R> setData(
        inputList: L? = null,
        dataComplete: Boolean,
        busy : Boolean,
        body: (DataDispatcherImpl.() -> Unit)? = null,
        receiver: suspend R.() -> Unit
    ):DataDispatcher
    abstract suspend fun getData(
        dataCategory: DataCategory,
        body: ((DataDispatcherImpl) -> Unit)? = null,
        receiver: suspend DataDispatcherImpl.() -> Unit
    ): DataDispatcher
}

data class DataDispatcherResultImpl(
    override val sender: String,
    override val recipient: String,
    override val count: Int
) : DataDispatcherResult

class DataDispatcherImpl() : DataDispatcher, DataFlowMeasurable {
    override var onResultsSubmit: ((ServiceCallResult) -> Unit)? = null
    override var onDataReceived: (DataDispatcherResult) -> Unit = {}
    override var onDataBeforeDispatch: (() -> Unit)? = null
    override var onDataAfterDispatch: ((CallResult) -> Unit)? = null
    override var onDataDispatchComplete: (() -> Unit)? = null
    override var onException: ((Exception) -> Unit)? = null
    override var onBuffered: (()->Unit)? = null
    override var onBufferRelease: (()->Unit)? = null

    override  fun init(onSubmitResult: ((ServiceCallResult) -> Unit)?) {
        onResultsSubmit = onSubmitResult
    }
    override fun <L> addToBuffer(dataItems: List<L>) {

    }
    override suspend fun <L, R> setData(
        inputList: L?,
        dataComplete: Boolean,
        busy: Boolean,
        body: (DataDispatcherImpl.() -> Unit)?,
        receiver: suspend R.() -> Unit
    ): DataDispatcher {
        val input = inputList
        if (body != null) {
            this.body()
        }
        if (busy) {
            onBuffered?.invoke()
        }
        return this
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

    fun getServiceTransferResult(callResult: DataServiceCallResult): ServiceCallResult {
        val result: ServiceCallResult = callResult
        if (onResultsSubmit != null){
            onResultsSubmit?.invoke(result)
        }
        return result
    }
}

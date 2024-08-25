package adminApi.com.general.classes

class ExecutionResult {

    var received:Int = 0
    var updated:Int = 0

    fun onComplete( fn: ()->Unit): ExecutionResult{
        if(received == updated){
            fn()
        }
        return this
    }

}
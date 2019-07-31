package jetbrains.livemap.core.multitasking

interface MicroTask<ItemT> {

    fun resume()
    fun alive(): Boolean
    fun getResult(): ItemT?

    fun <ResultT> map(success: (ItemT?) -> ResultT): MicroTask<ResultT> {
        return MicroTaskUtil.map(this, success)
    }

    fun <ResultT> flatMap(success: (ItemT?) -> MicroTask<ResultT>): MicroTask<ResultT> {
        return MicroTaskUtil.flatMap(this, success)
    }
}
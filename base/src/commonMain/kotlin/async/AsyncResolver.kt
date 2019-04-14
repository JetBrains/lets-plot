package jetbrains.datalore.base.async

internal interface AsyncResolver<ItemT> {
    fun success(result: ItemT)

    fun failure(t: Throwable)
}
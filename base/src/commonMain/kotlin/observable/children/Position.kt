package jetbrains.datalore.base.observable.children

interface Position<ChildT> {
    val role: Any
    fun get(): ChildT?
}
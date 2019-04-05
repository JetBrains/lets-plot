package jetbrains.datalore.base.observable.children

interface PositionData<ChildT> {
    fun get(): Position<ChildT>
    fun remove()
}
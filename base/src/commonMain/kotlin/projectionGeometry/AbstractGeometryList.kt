package jetbrains.datalore.base.projectionGeometry

open class AbstractGeometryList<T>(private val myGeometry: List<T>) : AbstractList<T>() {
    override fun get(index: Int): T {
        return myGeometry[index]
    }

    override val size: Int
        get() = myGeometry.size
}

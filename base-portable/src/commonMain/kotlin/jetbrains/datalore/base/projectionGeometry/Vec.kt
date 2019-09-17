package jetbrains.datalore.base.projectionGeometry

data class Vec<TypeT> internal constructor(
    val x: Double,
    val y: Double
) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
}

fun <T> explicitVec(x: Double, y: Double): Vec<T> = Vec(x, y)
fun <T> newVec(x: Scalar<T>, y: Scalar<T>): Vec<T> = Vec(x.value, y.value)

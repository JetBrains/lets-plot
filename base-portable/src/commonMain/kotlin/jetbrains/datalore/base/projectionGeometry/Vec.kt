package jetbrains.datalore.base.projectionGeometry

data class Vec<TypeT>(
    val x: Double,
    val y: Double
) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
}

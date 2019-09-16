package jetbrains.datalore.base.projectionGeometry

data class Vec<TypeT>(
    val x: Double,
    val y: Double
) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())

    fun add(p: Vec<TypeT>) =
        Vec<TypeT>(x + p.x, y + p.y)
    fun subtract(p: Vec<TypeT>) =
        Vec<TypeT>(x - p.x, y - p.y)
    fun mul(d: Double) = Vec<TypeT>(x * d, y * d)
}
package jetbrains.datalore.base.projectionGeometry

data class Rect<TypeT>(
    val origin: Vec<TypeT>,
    val dimension: Vec<TypeT>
) {
    constructor(
        left: Double,
        top: Double,
        width: Double,
        height: Double
    ) : this(
        Vec(left, top),
        Vec(width, height)
    )
}

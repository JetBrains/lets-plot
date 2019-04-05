package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property

class SvgGElement : SvgGraphicsElement(), SvgTransformable, SvgContainer {
    val elementName: String
        get() = "g"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    fun transform(): Property<SvgTransform> {
        return getAttribute(TRANSFORM)
    }

    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().invertTransform(this, point)
    }

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().applyTransform(this, point)
    }
}
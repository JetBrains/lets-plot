package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.vis.svg.SvgTransformable.Companion.TRANSFORM

class SvgDefsElement : SvgGraphicsElement(), SvgContainer,
    SvgTransformable {
    override val elementName = "defs"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    override fun transform(): Property<SvgTransform?> {
        return getAttribute(TRANSFORM)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }
}
package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgShape.Companion.FILL
import jetbrains.datalore.vis.svg.SvgShape.Companion.FILL_OPACITY
import jetbrains.datalore.vis.svg.SvgShape.Companion.STROKE
import jetbrains.datalore.vis.svg.SvgShape.Companion.STROKE_OPACITY
import jetbrains.datalore.vis.svg.SvgShape.Companion.STROKE_WIDTH
import jetbrains.datalore.vis.svg.SvgTransformable.Companion.TRANSFORM

class SvgEllipseElement() : SvgGraphicsElement(),
    SvgTransformable, SvgShape {

    companion object {
        val CX: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("cx")
        val CY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("cy")
        val RX: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("rx")
        val RY: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("ry")
    }

    override val elementName = "ellipse"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(cx: Double, cy: Double, rx: Double, ry: Double) : this() {

        setAttribute(CX, cx)
        setAttribute(CY, cy)
        setAttribute(RX, rx)
        setAttribute(RY, ry)
    }

    fun cx(): Property<Double?> {
        return getAttribute(CX)
    }

    fun cy(): Property<Double?> {
        return getAttribute(CY)
    }

    fun rx(): Property<Double?> {
        return getAttribute(RX)
    }

    fun ry(): Property<Double?> {
        return getAttribute(RY)
    }

    override fun transform(): Property<SvgTransform?> {
        return getAttribute(TRANSFORM)
    }

    override fun fill(): Property<SvgColor?> {
        return getAttribute(FILL)
    }

    override fun fillColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(fill(), fillOpacity())
    }

    override fun fillOpacity(): Property<Double?> {
        return getAttribute(FILL_OPACITY)
    }

    override fun stroke(): Property<SvgColor?> {
        return getAttribute(STROKE)
    }

    override fun strokeColor(): WritableProperty<Color?> {
        return SvgUtils.colorAttributeTransform(stroke(), strokeOpacity())
    }

    override fun strokeOpacity(): Property<Double?> {
        return getAttribute(STROKE_OPACITY)
    }

    override fun strokeWidth(): Property<Double?> {
        return getAttribute(STROKE_WIDTH)
    }

    override fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.invertTransform(this, point)
    }

    override fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer()!!.applyTransform(this, point)
    }
}
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

class SvgPathElement() : SvgGraphicsElement(), SvgTransformable,
    SvgShape {

    companion object {
        val D: SvgAttributeSpec<SvgPathData> =
            SvgAttributeSpec.createSpec("d")
    }

    override val elementName = "path"

    override val bBox: DoubleRectangle
        get() = container().getPeer()!!.getBBox(this)

    constructor(d: SvgPathData) : this() {
        setAttribute(D, d)
    }

    fun d(): Property<SvgPathData?> {
        return getAttribute(D)
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
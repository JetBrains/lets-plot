package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

class SvgPathElement() : SvgGraphicsElement(), SvgTransformable, SvgShape {

    val elementName: String
        get() = "path"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    constructor(d: SvgPathData) : this() {

        setAttribute(D, d)
    }

    fun d(): Property<SvgPathData> {
        return getAttribute(D)
    }

    fun transform(): Property<SvgTransform> {
        return getAttribute(TRANSFORM)
    }

    fun fill(): Property<SvgColor> {
        return getAttribute(FILL)
    }

    fun fillColor(): WritableProperty<Color> {
        return SvgUtils.colorAttributeTransform(fill(), fillOpacity())
    }

    fun fillOpacity(): Property<Double> {
        return getAttribute(FILL_OPACITY)
    }

    fun stroke(): Property<SvgColor> {
        return getAttribute(STROKE)
    }

    fun strokeColor(): WritableProperty<Color> {
        return SvgUtils.colorAttributeTransform(stroke(), strokeOpacity())
    }

    fun strokeOpacity(): Property<Double> {
        return getAttribute(STROKE_OPACITY)
    }

    fun strokeWidth(): Property<Double> {
        return getAttribute(STROKE_WIDTH)
    }

    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().invertTransform(this, point)
    }

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().applyTransform(this, point)
    }

    companion object {
        private val D = SvgAttributeSpec.createSpec("d")
    }
}
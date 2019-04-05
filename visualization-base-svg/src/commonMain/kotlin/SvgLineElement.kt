package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

class SvgLineElement() : SvgGraphicsElement(), SvgTransformable, SvgShape {

    val elementName: String
        get() = "line"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    constructor(x1: Double, y1: Double, x2: Double, y2: Double) : this() {

        setAttribute(X1, x1)
        setAttribute(Y1, y1)
        setAttribute(X2, x2)
        setAttribute(Y2, y2)
    }

    fun x1(): Property<Double> {
        return getAttribute(X1)
    }

    fun y1(): Property<Double> {
        return getAttribute(Y1)
    }

    fun x2(): Property<Double> {
        return getAttribute(X2)
    }

    fun y2(): Property<Double> {
        return getAttribute(Y2)
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
        private val X1 = SvgAttributeSpec.createSpec("x1")
        private val Y1 = SvgAttributeSpec.createSpec("y1")
        private val X2 = SvgAttributeSpec.createSpec("x2")
        private val Y2 = SvgAttributeSpec.createSpec("y2")
    }
}
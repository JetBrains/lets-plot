package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

class SvgRectElement() : SvgGraphicsElement(), SvgTransformable, SvgShape {

    val elementName: String
        get() = "rect"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    constructor(x: Double, y: Double, width: Double, height: Double) : this() {

        setAttribute(X, x)
        setAttribute(Y, y)
        setAttribute(HEIGHT, height)
        setAttribute(WIDTH, width)
    }

    constructor(rect: Rectangle) : this(rect.origin.x, rect.origin.y, rect.dimension.x, rect.dimension.y) {}

    constructor(rect: DoubleRectangle) : this(rect.origin.x, rect.origin.y, rect.dimension.x, rect.dimension.y) {}

    fun x(): Property<Double> {
        return getAttribute(X)
    }

    fun y(): Property<Double> {
        return getAttribute(Y)
    }

    fun height(): Property<Double> {
        return getAttribute(HEIGHT)
    }

    fun width(): Property<Double> {
        return getAttribute(WIDTH)
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
        private val X = SvgAttributeSpec.createSpec("x")
        private val Y = SvgAttributeSpec.createSpec("y")
        private val WIDTH = SvgAttributeSpec.createSpec("width")
        private val HEIGHT = SvgAttributeSpec.createSpec("height")
    }
}
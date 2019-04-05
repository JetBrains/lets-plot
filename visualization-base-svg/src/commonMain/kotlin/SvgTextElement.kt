package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

class SvgTextElement() : SvgGraphicsElement(), SvgTransformable, SvgTextContent {

    val elementName: String
        get() = "text"

    val computedTextLength: Double
        get() = container().getPeer().getComputedTextLength(this)

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    constructor(content: String) : this() {

        setTextNode(content)
    }

    constructor(x: Double, y: Double, content: String) : this() {

        setAttribute(X, x)
        setAttribute(Y, y)
        setTextNode(content)
    }

    fun x(): Property<Double> {
        return getAttribute(X)
    }

    fun y(): Property<Double> {
        return getAttribute(Y)
    }

    fun transform(): Property<SvgTransform> {
        return getAttribute(TRANSFORM)
    }

    fun setTextNode(text: String) {
        children().clear()
        addTextNode(text)
    }

    fun addTextNode(text: String) {
        val textNode = SvgTextNode(text)
        children().add(textNode)
    }

    fun setTSpan(tspan: SvgTSpanElement) {
        children().clear()
        addTSpan(tspan)
    }

    fun setTSpan(text: String) {
        children().clear()
        addTSpan(text)
    }

    fun addTSpan(tspan: SvgTSpanElement) {
        children().add(tspan)
    }

    fun addTSpan(text: String) {
        children().add(SvgTSpanElement(text))
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
    }
}
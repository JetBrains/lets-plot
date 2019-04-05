package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color

class SvgTSpanElement() : SvgElement(), SvgTextContent {

    val elementName: String
        get() = "tspan"

    val computedTextLength: Double
        get() = container().getPeer().getComputedTextLength(this)

    constructor(text: String) : this() {

        setText(text)
    }

    constructor(x: Double, y: Double, text: String) : this(text) {

        setAttribute(X, x)
        setAttribute(Y, y)
    }

    fun x(): Property<Double> {
        return getAttribute(X)
    }

    fun y(): Property<Double> {
        return getAttribute(Y)
    }

    fun setText(text: String) {
        children().clear()
        addText(text)
    }

    fun addText(text: String) {
        val node = SvgTextNode(text)
        children().add(node)
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

    companion object {
        private val X = SvgAttributeSpec.createSpec("x")
        private val Y = SvgAttributeSpec.createSpec("y")
    }
}
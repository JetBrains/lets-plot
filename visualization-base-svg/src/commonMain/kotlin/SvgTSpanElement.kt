package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgShape.Companion.FILL
import jetbrains.datalore.visualization.base.svg.SvgShape.Companion.FILL_OPACITY
import jetbrains.datalore.visualization.base.svg.SvgShape.Companion.STROKE
import jetbrains.datalore.visualization.base.svg.SvgShape.Companion.STROKE_OPACITY
import jetbrains.datalore.visualization.base.svg.SvgShape.Companion.STROKE_WIDTH

class SvgTSpanElement() : SvgElement(), SvgTextContent {

    companion object {
        private val X: SvgAttributeSpec<Double> = SvgAttributeSpec.createSpec("x")
        private val Y: SvgAttributeSpec<Double> = SvgAttributeSpec.createSpec("y")
    }

    override val elementName = "tspan"

    override val computedTextLength: Double
        get() = container().getPeer().getComputedTextLength(this)

    constructor(text: String) : this() {

        setText(text)
    }

    constructor(x: Double, y: Double, text: String) : this(text) {

        setAttribute(X, x)
        setAttribute(Y, y)
    }

    fun x(): Property<Double?> {
        return getAttribute(X)
    }

    fun y(): Property<Double?> {
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
}
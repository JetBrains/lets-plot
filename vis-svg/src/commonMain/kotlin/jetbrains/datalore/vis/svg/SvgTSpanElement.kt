package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.FILL
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.FILL_OPACITY
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.STROKE
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.STROKE_OPACITY
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.STROKE_WIDTH
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.TEXT_ANCHOR
import jetbrains.datalore.vis.svg.SvgTextContent.Companion.TEXT_DY

class SvgTSpanElement() : SvgElement(), SvgTextContent {

    companion object {
        private val X: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("x")
        private val Y: SvgAttributeSpec<Double> =
            SvgAttributeSpec.createSpec("y")
    }

    override val elementName = "tspan"

    override val computedTextLength: Double
        get() = container().getPeer()!!.getComputedTextLength(this)

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

    override fun textAnchor(): Property<String?> {
        return getAttribute(TEXT_ANCHOR)
    }

    override fun textDy(): Property<String?> {
        return getAttribute(TEXT_DY)
    }
}
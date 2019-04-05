package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.Property

import jetbrains.datalore.visualization.base.svg.SvgConstants.SVG_STYLE_ATTRIBUTE

class SvgImageElement() : SvgGraphicsElement(), SvgTransformable {

    val elementName: String
        get() = "image"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    init {
        setAttribute(PRESERVE_ASPECT_RATIO, "none")
        setAttribute(SVG_STYLE_ATTRIBUTE, "image-rendering: pixelated;")
    }

    constructor(x: Double, y: Double, width: Double, height: Double) : this() {

        setAttribute(X, x)
        setAttribute(Y, y)
        setAttribute(WIDTH, width)
        setAttribute(HEIGHT, height)
    }

    fun x(): Property<Double> {
        return getAttribute(X)
    }

    fun y(): Property<Double> {
        return getAttribute(Y)
    }

    fun width(): Property<Double> {
        return getAttribute(WIDTH)
    }

    fun height(): Property<Double> {
        return getAttribute(HEIGHT)
    }

    fun href(): Property<String> {
        return getAttribute(HREF)
    }

    fun preserveAspectRatio(): Property<String> {
        return getAttribute(PRESERVE_ASPECT_RATIO)
    }

    fun transform(): Property<SvgTransform> {
        return getAttribute(TRANSFORM)
    }

    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().invertTransform(this, point)
    }

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().applyTransform(this, point)
    }

    companion object {
        private val XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink"
        private val XLINK_PREFIX = "xlink"

        private val X = SvgAttributeSpec.createSpec("x")
        private val Y = SvgAttributeSpec.createSpec("y")
        private val WIDTH = SvgAttributeSpec.createSpec("width")
        private val HEIGHT = SvgAttributeSpec.createSpec("height")

        val HREF = SvgAttributeSpec.createSpecNS("href", XLINK_PREFIX, XLINK_NAMESPACE_URI)
        private val PRESERVE_ASPECT_RATIO = SvgAttributeSpec.createSpec("preserveAspectRatio")
    }
}

package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.WritableProperty

class SvgSvgElement() : SvgStylableElement(), SvgContainer, SvgLocatable {

    val elementName: String
        get() = "svg"

    val bBox: DoubleRectangle
        get() = container().getPeer().getBBox(this)

    constructor(width: Double, height: Double) : this() {

        setAttribute(WIDTH, width)
        setAttribute(HEIGHT, height)
    }

    fun setStyle(css: SvgCssResource) {
        children().add(SvgStyleElement(css))
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

    fun viewBox(): Property<ViewBoxRectangle> {
        return getAttribute(VIEW_BOX)
    }

    fun viewBoxRect(): WritableProperty<DoubleRectangle> {
        return { value -> viewBox().set(ViewBoxRectangle(value)) }
    }

    fun opacity(): Property<Double> {
        return getAttribute(OPACITY)
    }

    fun clipPath(): Property<SvgIRI> {
        return getAttribute(CLIP_PATH)
    }

    fun pointToTransformedCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().invertTransform(this, point)
    }

    fun pointToAbsoluteCoordinates(point: DoubleVector): DoubleVector {
        return container().getPeer().applyTransform(this, point)
    }


    class ViewBoxRectangle {
        private var myX: Double = 0.toDouble()
        private var myY: Double = 0.toDouble()
        private var myWidth: Double = 0.toDouble()
        private var myHeight: Double = 0.toDouble()

        constructor(x: Double, y: Double, width: Double, height: Double) {
            myX = x
            myY = y
            myWidth = width
            myHeight = height
        }

        constructor(rect: DoubleRectangle) {
            myX = rect.origin.x
            myY = rect.origin.y
            myWidth = rect.dimension.x
            myHeight = rect.dimension.y
        }

        constructor(rect: Rectangle) {
            myX = rect.origin.x
            myY = rect.origin.y
            myWidth = rect.dimension.x
            myHeight = rect.dimension.y
        }

        override fun toString(): String {
            return "$myX $myY $myWidth $myHeight"
        }
    }

    companion object {
        private val X = SvgAttributeSpec.createSpec("x")
        private val Y = SvgAttributeSpec.createSpec("y")
        private val WIDTH = SvgAttributeSpec.createSpec(SvgConstants.WIDTH)
        private val HEIGHT = SvgAttributeSpec.createSpec(SvgConstants.HEIGHT)
        private val VIEW_BOX = SvgAttributeSpec.createSpec("viewBox")
    }
}
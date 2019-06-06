package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton.*
import javafx.scene.input.MouseEvent
import javafx.scene.shape.*
import javafx.scene.text.Text
import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.visualization.base.svg.*


object Utils {
    fun elementChildren(e: Group): MutableList<Node> {
        return object : AbstractMutableList<Node>() {
            override val size: Int
                get() = e.children.size

            override fun get(index: Int): Node {
                return e.children[index]
            }

            override fun set(index: Int, element: Node): Node {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                return e.children.set(index, element)
            }

            override fun add(index: Int, element: Node) {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                e.children.add(index, element)
            }

            override fun removeAt(index: Int): Node {
                return e.children.removeAt(index)
            }
        }
    }

    fun newSceneNode(source: SvgNode): Node {
        return when (source) {
            is SvgEllipseElement -> Ellipse()
            is SvgCircleElement -> Circle()
            is SvgRectElement -> Rectangle()
            is SvgTextElement -> Text()
            is SvgPathElement -> SVGPath()
            is SvgLineElement -> Line()
//            is SvgSvgElement -> SVGOMSVGElement(null, myDoc)
            is SvgGElement -> Group()
            is SvgStyleElement -> Group()          // ignore
//            is SvgTextNode -> myDoc.createTextNode(null)
//            is SvgTSpanElement -> SVGOMTSpanElement(null, myDoc)
//            is SvgDefsElement -> SVGOMDefsElement(null, myDoc)
//            is SvgClipPathElement -> SVGOMClipPathElement(null, myDoc)
            is SvgImageElement -> ImageView()
            else -> throw IllegalArgumentException("Unsupported source svg element: ${source.javaClass.simpleName}")
        }
    }

    fun getButton(evt: MouseEvent): Button {
        return when (evt.button) {
            PRIMARY -> Button.LEFT
            MIDDLE -> Button.MIDDLE
            SECONDARY -> Button.RIGHT
            else -> Button.NONE
        }
    }

    fun getModifiers(evt: MouseEvent): KeyModifiers {
        val ctrlKey = evt.isControlDown
        val altKey = evt.isAltDown
        val shiftKey = evt.isShiftDown
        val metaKey = evt.isMetaDown
        return KeyModifiers(ctrlKey, altKey, shiftKey, metaKey)
    }
}
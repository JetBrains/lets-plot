package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.collections.ObservableList
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.*
import javafx.scene.text.Text
import jetbrains.datalore.base.event.Button
import jetbrains.datalore.base.event.KeyModifiers
import jetbrains.datalore.visualization.base.svg.*


object Utils {
    fun elementChildren(e: Parent): MutableList<Node> {
        return object : AbstractMutableList<Node>() {
            override val size: Int
                get() = getChildren(e).size

            override fun get(index: Int): Node {
                return getChildren(e)[index]
            }

            override fun set(index: Int, element: Node): Node {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                return getChildren(e).set(index, element)
            }

            override fun add(index: Int, element: Node) {
                if (element.parent != null) {
                    throw IllegalStateException()
                }
                getChildren(e).add(index, element)
            }

            override fun removeAt(index: Int): Node {
                return getChildren(e).removeAt(index)
            }
        }
    }

    fun getChildren(parent: Parent): ObservableList<Node> {
        return when (parent) {
            is Group -> parent.children
            is Pane -> parent.children
            else -> throw IllegalArgumentException("Unsupported parent typr: ${parent.javaClass.simpleName}")
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
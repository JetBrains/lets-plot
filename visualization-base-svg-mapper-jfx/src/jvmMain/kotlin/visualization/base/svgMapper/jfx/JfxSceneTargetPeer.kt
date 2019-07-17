package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.scene.shape.Rectangle
import javafx.scene.shape.SVGPath
import javafx.scene.text.Text
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgTextNode
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimNode
import jetbrains.datalore.visualization.base.svgMapper.TargetPeer

internal class JfxSceneTargetPeer : TargetPeer<Node> {
    override fun appendChild(target: Node, child: Node) {
        Utils.getChildren(target as Parent).add(child)
    }

    override fun removeAllChildren(target: Node) {
        if (target is Parent) {
            Utils.getChildren(target).clear()
        }
    }

    override fun newSvgElement(source: SvgElement): Node {
        return Utils.newSceneNode(source)
    }

    override fun newSvgTextNode(source: SvgTextNode): Node {
        return Text(source.textContent().get())
    }

    override fun newSvgSlimNode(source: SvgSlimNode): Node {
        return when (source.elementName) {
            SvgSlimElements.GROUP -> Group()
            SvgSlimElements.LINE -> Line()
            SvgSlimElements.CIRCLE -> Circle()
            SvgSlimElements.RECT -> Rectangle()
            SvgSlimElements.PATH -> SVGPath()
            else -> throw IllegalStateException("Unsupported slim node " + source::class.simpleName + " '" + source.elementName + "'")
        }
    }

    override fun setAttribute(target: Node, name: String, value: String) {
        Utils.setAttribute(target, name, value)
    }

    override fun hookEventHandlers(source: SvgElement, target: Node, eventSpecs: Set<SvgEventSpec>): Registration {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.visualization.base.svg.SvgNode

internal class GroupJava(initialCapacity: Int) :
        ElementJava(SvgSlimElements.GROUP),
        SvgSlimGroup {

    private val myChildren: MutableList<ElementJava> = ArrayList(initialCapacity)

    override val slimChildren: Iterable<SvgSlimNode>
        get() = myChildren.map { v -> v }

    constructor(initialCapacity: Int, transform: Any) : this(initialCapacity) {
        setAttribute(SlimBase.transform, transform)
    }

    private fun draw(context: CanvasContext) {
        context.push(getAttribute(transform))
        for (child in myChildren) {
            SvgSlimRenderer.draw(child, context)
        }
        context.restore()
    }

    internal fun addChild(o: ElementJava) {
        myChildren.add(o)
    }

    override fun asDummySvgNode(): SvgNode {
        return MyDummySvgNode(this)
    }

    private class MyDummySvgNode internal constructor(private val myGroup: GroupJava) :
            DummySvgNode(),
            SvgSlimNode,
            CanvasAware {

        override val elementName: String
            get() = myGroup.elementName

        override val attributes: Iterable<SvgSlimNode.Attr>
            get() = myGroup.attributes

        override val slimChildren: Iterable<SvgSlimNode>
            get() = myGroup.slimChildren

        override fun draw(context: CanvasContext) {
            myGroup.draw(context)
        }
    }
}

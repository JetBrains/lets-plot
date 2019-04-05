package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.visualization.base.svg.SvgNode

import java.util.ArrayList

internal class GroupNative(initialCapacity: Int) : ElementNative(SvgSlimElements.GROUP), SvgSlimGroup {
    private val myChildren: MutableList<ElementNative>

    init {
        myChildren = ArrayList<ElementNative>(initialCapacity)
    }

    constructor(initialCapacity: Int, transform: Any) : this(initialCapacity) {
        setAttribute(SlimBase.transform, transform)
    }

    protected fun hasInnerTextContent(): Boolean {
        return true
    }

    protected fun appendInnerTextContentTo(sb: StringBuffer) {
        for (child in myChildren) {
            child.appendTo(sb)
        }
    }

    private fun draw(context: CanvasContext) {
        context.push(getAttribute(transform))
        for (child in myChildren) {
            SvgSlimRenderer.draw(child, context)
        }
        context.restore()
    }

    protected fun addChild(o: ElementNative) {
        myChildren.add(o)
    }

    fun asDummySvgNode(): SvgNode {
        return MyDummySvgNode(this)
    }

    private class MyDummySvgNode internal constructor(private val myGroup: GroupNative) : DummySvgNode(), WithTextGen, CanvasAware {

        fun appendTo(sb: StringBuffer) {
            myGroup.appendTo(sb)
        }

        fun draw(context: CanvasContext) {
            myGroup.draw(context)
        }
    }
}

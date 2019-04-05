package jetbrains.datalore.visualization.base.svg.slim

import com.google.common.collect.Lists
import jetbrains.datalore.visualization.base.svg.SvgNode

import java.util.ArrayList

internal class GroupJava(initialCapacity: Int) : ElementJava(SvgSlimElements.GROUP), SvgSlimGroup {
    private val myChildren: MutableList<ElementJava>

    val children: Iterable<SvgSlimNode>
        get() = Lists.transform(myChildren, { v -> v })

    init {
        myChildren = ArrayList<ElementJava>(initialCapacity)
    }

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

    protected fun addChild(o: ElementJava) {
        myChildren.add(o)
    }

    fun asDummySvgNode(): SvgNode {
        return MyDummySvgNode(this)
    }

    private class MyDummySvgNode internal constructor(private val myGroup: GroupJava) : DummySvgNode(), SvgSlimNode, CanvasAware {

        val elementName: String
            get() = myGroup.getElementName()

        val attributes: Iterable<Attr>
            get() = myGroup.getAttributes()

        val children: Iterable<SvgSlimNode>
            get() = myGroup.children

        fun draw(context: CanvasContext) {
            myGroup.draw(context)
        }
    }
}

package jetbrains.datalore.vis.svg.slim

import jetbrains.datalore.vis.svg.SvgNode

internal class GroupJava(initialCapacity: Int) :
        ElementJava(SvgSlimElements.GROUP),
    SvgSlimGroup {

    private val myChildren: MutableList<ElementJava> = ArrayList(initialCapacity)

    override val slimChildren: Iterable<SvgSlimNode>
        get() = myChildren.map { v -> v }

    constructor(initialCapacity: Int, transform: Any) : this(initialCapacity) {
        setAttribute(Companion.transform, transform)
    }

    internal fun addChild(o: ElementJava) {
        myChildren.add(o)
    }

    override fun asDummySvgNode(): SvgNode {
        return MyDummySvgNode(this)
    }

    private class MyDummySvgNode internal constructor(private val myGroup: GroupJava) :
            DummySvgNode(),
        SvgSlimNode {

        override val elementName: String
            get() = myGroup.elementName

        override val attributes: Iterable<SvgSlimNode.Attr>
            get() = myGroup.attributes

        override val slimChildren: Iterable<SvgSlimNode>
            get() = myGroup.slimChildren

    }
}

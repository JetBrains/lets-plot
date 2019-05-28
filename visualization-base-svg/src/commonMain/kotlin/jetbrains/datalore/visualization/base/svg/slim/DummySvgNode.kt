package jetbrains.datalore.visualization.base.svg.slim

import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.visualization.base.svg.SvgNode

internal open class DummySvgNode : SvgNode() {
    init {
        isPrebuiltSubtree = true
    }

    override fun children(): ObservableList<SvgNode> {
        val children = super.children()
        if (!children.isEmpty()) {
            throw IllegalStateException("Can't have children")
        }
        return children
    }
}

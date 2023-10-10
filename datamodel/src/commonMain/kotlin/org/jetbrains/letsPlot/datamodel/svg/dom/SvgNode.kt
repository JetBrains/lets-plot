/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.commons.intern.observable.children.ChildList
import org.jetbrains.letsPlot.commons.intern.observable.children.SimpleComposite
import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList


abstract class SvgNode : SimpleComposite<SvgNode?, SvgNode>() {
    private var myContainer: SvgNodeContainer? = null

    private var myChildren: SvgChildList? = null

    /**
     * @return `true` if subtree below this node is absolutely not going to
     * change once the process of mapping is completed.
     */
    /**
     * Marks the node as whether the descendant nodes can change
     * their properties/structure at any time (this is the default)
     * or properties/structure of descendant nodes are absolutely not going to
     * change once the process of their mapping is completed (i.e. the subtree is 'pre-built').
     *
     *
     * If subtree is 'pre-build' then listening for changes in structure/properties of nodes in the subtree
     * is not required and can be dropped for better performance.
     *
     * @param prebuiltSubtree If `false` (default) the subtree is 'dynamic'.
     * If `true` the subtree is 'static' (pre-built)
     */
    var isPrebuiltSubtree: Boolean = false

    fun isAttached(): Boolean = myContainer != null

    fun container(): SvgNodeContainer {
        return myContainer!!
    }

    // Useful for debugging
    fun root(): SvgNode {
        var root: SvgNode = this
        while (root.parent().get() != null) {
            root = root.parent().get()!!
        }
        return root
    }

    open fun children(): ObservableList<SvgNode> {
        if (myChildren == null) {
            myChildren = SvgChildList(this)
        }
        return myChildren as ObservableList<SvgNode>
    }

    internal fun attach(container: SvgNodeContainer?) {
        if (isAttached()) {
            throw IllegalStateException("Svg element is already attached")
        }

        for (node in children()) {
            node.attach(container)
        }

        myContainer = container
        myContainer!!.svgNodeAttached(this)
    }

    internal fun detach() {
        if (!isAttached()) {
            throw IllegalStateException("Svg element is not attached")
        }

        for (node in children()) {
            node.detach()
        }

        myContainer!!.svgNodeDetached(this)
        myContainer = null
    }

    private inner class SvgChildList internal constructor(parent: SvgNode) :

            ChildList<SvgNode, SvgNode>(parent) {

        override fun beforeItemAdded(index: Int, item: SvgNode) {
            if (isAttached()) {
                item.attach(container())
            }
            super.beforeItemAdded(index, item)
        }

        override fun beforeItemSet(index: Int, oldItem: SvgNode, newItem: SvgNode) {
            if (isAttached()) {
                oldItem.detach()
                newItem.attach(container())
            }
            super.beforeItemSet(index, oldItem, newItem)
        }

        override fun beforeItemRemoved(index: Int, item: SvgNode) {
            if (isAttached()) {
                item.detach()
            }
            super.beforeItemRemoved(index, item)
        }
    }
}
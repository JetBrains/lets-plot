/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

import org.jetbrains.letsPlot.commons.intern.observable.collections.list.ObservableList
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode

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

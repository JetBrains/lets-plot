/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg.slim

import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.vis.svg.SvgNode

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

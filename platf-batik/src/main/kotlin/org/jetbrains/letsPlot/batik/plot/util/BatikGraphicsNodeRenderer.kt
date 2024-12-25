/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.plot.util

import org.apache.batik.gvt.GraphicsNode
import java.awt.Dimension
import java.awt.Graphics2D

interface BatikGraphicsNodeRenderer {
    val priority: Int
    fun paint(node: GraphicsNode, g: Graphics2D, size: Dimension)

    object Default : BatikGraphicsNodeRenderer {
        override val priority: Int
            get() = 0

        override fun paint(node: GraphicsNode, g: Graphics2D, size: Dimension) {
            node.paint(g)
        }
    }

    companion object {
        fun getInstance(): BatikGraphicsNodeRenderer {
            val instances = ServiceLoaderHelper.loadInstances<BatikGraphicsNodeRenderer>()
            return instances.maxByOrNull { it.priority } ?: Default
        }
    }
}

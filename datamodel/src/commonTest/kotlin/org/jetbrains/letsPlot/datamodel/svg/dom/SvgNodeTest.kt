/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import kotlin.test.*


class SvgNodeTest {
    @Test
    fun nodeAdd() {
        val node1 = newNode()
        val node2 = newNode()

        assertNull(node1.parent().get())

        node1.children().add(node2)

        assertSame(node1, node2.parent().get())
        assertEquals(1, node1.children().size)
        assertSame(node1.children()[0], node2)
    }

    @Test
    fun nodeRemove() {
        val node1 = newNode()
        val node2 = newNode()

        node1.children().add(node2)
        node1.children().remove(node2)

        assertNull(node2.parent().get())
        assertTrue(node1.children().isEmpty())
    }

    private fun newNode(): SvgNode {
        return MySvgNode()
    }

    private class MySvgNode : SvgNode()
}
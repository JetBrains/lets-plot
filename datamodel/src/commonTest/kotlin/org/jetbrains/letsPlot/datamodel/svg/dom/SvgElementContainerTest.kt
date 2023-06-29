/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.function.Value
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import kotlin.test.*

class SvgElementContainerTest {
    private val root = SvgSvgElement()
    private val container = SvgNodeContainer(root)

    private fun assertNPE(block: () -> Unit) {
        assertFailsWith(NullPointerException::class) {
            block.invoke()
        }
    }

    @Test
    fun root() {
        assertSame(container.root().get(), root)
        assertTrue(root.isAttached())
        assertSame(container, root.container())
    }

    @Test
    fun changeRoot() {
        val newRoot = SvgSvgElement()
        container.root().set(newRoot)
        assertTrue(newRoot.isAttached())
        assertSame(newRoot.container(), container)

        assertFalse(root.isAttached())
        assertNPE {
            root.container()
        }
//        assertNull(root.container())
    }

    @Test
    fun attach() {
        val node = newNode()
//        assertNull(node.container())
        assertNPE {
            node.container()
        }
        assertFalse(node.isAttached())

        root.children().add(node)
        assertTrue(node.isAttached())
        assertSame(node.container(), container)
    }

    @Test
    fun detach() {
        val node = newNode()
        root.children().add(node)
        root.children().remove(node)
        assertFalse(node.isAttached())
//        assertNull(node.container())
        assertNPE {
            node.container()
        }
    }

    @Test
    fun attachWithChildren() {
        val node1 = newNode()
        val node2 = newNode()
        node1.children().add(node2)
        assertFalse(node2.isAttached())

        root.children().add(node1)
        assertTrue(node2.isAttached())
    }

    @Test
    fun detachWithChildren() {
        val node1 = newNode()
        val node2 = newNode()
        node1.children().add(node2)

        root.children().add(node1)
        root.children().remove(node1)
        assertFalse(node2.isAttached())
//        assertNull(node2.container())
        assertNPE {
            node2.container()
        }
    }

    @Test
    fun attachEvent() {
        val nodeAttached = Value(false)
        container.addListener(object : SvgNodeContainerAdapter() {
            override fun onNodeAttached(node: SvgNode) {
                nodeAttached.set(true)
            }
        })
        val node = newNode()
        root.children().add(node)
        assertTrue(nodeAttached.get())
    }

    @Test
    fun detachEvent() {
        val nodeDetached = Value(false)
        container.addListener(object : SvgNodeContainerAdapter() {
            override fun onNodeDetached(node: SvgNode) {
                nodeDetached.set(true)
            }
        })
        val node = newNode()
        root.children().add(node)
        assertFalse(nodeDetached.get())
        root.children().remove(node)
        assertTrue(nodeDetached.get())
    }

    @Test
    fun alreadyAttachedException() {
        assertFailsWith(IllegalStateException::class) {
            root.attach(container)
        }
    }

    @Test
    fun notAttachedDetachException() {
        assertFailsWith(IllegalStateException::class) {
            newNode().detach()
        }
    }

    @Test
    fun attributeSetEvent() {
        val elem = SvgEllipseElement()
        root.children().add(elem)

        val isAttributeSet = Value(false)
        container.addListener(object : SvgNodeContainerAdapter() {
            override fun onAttributeSet(element: SvgElement, event: SvgAttributeEvent<*>) {
                isAttributeSet.set(true)
            }
        })

        elem.setAttribute("attr", "value")
        assertTrue(isAttributeSet.get())

        isAttributeSet.set(false)
        elem.cx().set(10.0)
        assertTrue(isAttributeSet.get())
    }

    private fun newNode(): SvgNode {
        return MySvgNode()
    }

    private class MySvgNode : SvgNode()
}
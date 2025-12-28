/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import kotlin.reflect.KProperty
import kotlin.test.Test
import kotlin.test.assertEquals

class HierarchicalPropertiesSynchronizationTest {

    /**
     * A concrete Element for testing.
     * Inherits Container so it can be a parent to other nodes.
     */
    internal class Node(initialTag: String) : Container() {
        // A local property. Changing this simulates changing 'transform'.
        var tag: String by visualProp(initialTag)

        // Custom version counter for 'tag'.
        // We use this instead of 'localTransformVersion' to decouple from Element's internals.
        var tagVersion = 0

        // The Hierarchical Property
        // We tell it to watch 'tagVersion'.
        val path: String by hierarchicalProp(Node::path, { tagVersion }) { parentPath ->
            (parentPath ?: "") + "/" + tag
        }

        override fun onPropertyChanged(prop: KProperty<*>) {
            super.onPropertyChanged(prop)

            // When 'tag' changes, we increment our version.
            // This mirrors how Element increments 'localTransformVersion' when 'transform' changes.
            if (prop == Node::tag) {
                tagVersion++
            }
        }

        override fun calculateLocalBBox(): DoubleRectangle = DoubleRectangle.ZERO
    }

    @Test
    fun `chain propagation - root-child-grandchild should update when root changes`() {
        // Setup: Root -> Child -> GrandChild
        val root = Node("root")
        val child = Node("child").apply { parent = root }
        val grandChild = Node("gc").apply { parent = child }

        // 1. Initial State
        // The property should pull values down the chain: /root/child/gc
        assertEquals("/root/child/gc", grandChild.path)

        // 2. Modify Root
        // 'visualProp' will detect the change ("root" != "newRoot"),
        // fire onPropertyChanged, and 'tagVersion' will increment.
        root.tag = "newRoot"

        // 3. Verify Update
        // grandChild.path calls getValue() ->
        // checks parent (child.path) -> checks parent (root.path) ->
        // root sees version change -> recomputes -> child recomputes -> grandChild recomputes.
        assertEquals("/newRoot/child/gc", grandChild.path)
    }

    @Test
    fun `local update - child should update when only child changes`() {
        val root = Node("root")
        val child = Node("child").apply { parent = root }

        assertEquals("/root/child", child.path)

        // Modify Child only
        child.tag = "newChild"

        // Should update based on local version change
        assertEquals("/root/newChild", child.path)
    }

    @Test
    fun `branching - two children with same parent should both update`() {
        val root = Node("root")
        val childA = Node("A").apply { parent = root }
        val childB = Node("B").apply { parent = root }

        assertEquals("/root/A", childA.path)
        assertEquals("/root/B", childB.path)

        // Modify Root
        root.tag = "base"

        // Both branches should see the change
        assertEquals("/base/A", childA.path)
        assertEquals("/base/B", childB.path)
    }

    @Test
    fun `deep chain - long hierarchy propagation`() {
        // A -> B -> C -> D -> E
        val a = Node("a")
        val b = Node("b").apply { parent = a }
        val c = Node("c").apply { parent = b }
        val d = Node("d").apply { parent = c }
        val e = Node("e").apply { parent = d }

        assertEquals("/a/b/c/d/e", e.path)

        // Change 'a'
        a.tag = "X"

        assertEquals("/X/b/c/d/e", e.path)
    }

    @Test
    fun `re-parenting - moving a subtree should update path`() {
        val root1 = Node("root1")
        val root2 = Node("root2")
        val child = Node("child")

        // Attach to root1
        child.parent = root1
        assertEquals("/root1/child", child.path)

        // Move to root2
        // Changing 'parent' is handled by Element structure logic.
        // The HierarchicalProperty.getValue() always fetches the *current* parent.
        child.parent = root2

        assertEquals("/root2/child", child.path)
    }
}
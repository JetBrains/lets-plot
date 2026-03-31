/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import kotlin.test.Test

class CtmAttributeSynchronizationTest {
    internal fun g(id: String, transform: AffineTransform = AffineTransform.IDENTITY): Group {
        val g = Group()
        g.id = id
        g.transform = transform
        return g
    }

    @Test
    fun `chain propagation - root-child-grandchild should update when root changes`() {
        // Setup: Root -> Child -> GrandChild
        val root = g("root", AffineTransform.makeTranslation(1, 1))

        val child = g("child", AffineTransform.makeTranslation(2, 2))
        root.children.add(child)

        val grandChild = g("gc", AffineTransform.makeTranslation(3, 3))
        child.children.add(grandChild)

        assertThat(grandChild.ctm).isEqualTo(AffineTransform.makeTranslation(6, 6))
    }

    @Test
    fun `local update - child should update when only child changes`() {
        val root = g("root", AffineTransform.makeTranslation(1, 1))
        val child = g("child", AffineTransform.makeTranslation(2, 2))
        root.children.add(child)

        assertThat(child.ctm).isEqualTo(AffineTransform.makeTranslation(3, 3))

        // Modify Child only
        child.transform = AffineTransform.makeTranslation(3, 3)

        assertThat(child.ctm).isEqualTo(AffineTransform.makeTranslation(4, 4))
    }

    @Test
    fun `branching - two children with same parent should both update`() {
        val root = g("root", AffineTransform.makeTranslation(1, 1))
        val childA = g("A", AffineTransform.makeTranslation(2, 2))
        root.children.add(childA)

        val childB = g("B", AffineTransform.makeTranslation(3, 3))
        root.children.add(childB)

        assertThat(childA.ctm).isEqualTo(AffineTransform.makeTranslation(3, 3))
        assertThat(childB.ctm).isEqualTo(AffineTransform.makeTranslation(4, 4))

        // Modify Root
        root.transform = AffineTransform.makeTranslation(10, 10)

        // Both branches should see the change
        assertThat(childA.ctm).isEqualTo(AffineTransform.makeTranslation(12, 12))
        assertThat(childB.ctm).isEqualTo(AffineTransform.makeTranslation(13, 13))
    }

    @Test
    fun `deep chain - long hierarchy propagation`() {
        // A -> B -> C -> D -> E
        val a = g("a", AffineTransform.makeTranslation(1, 1))
        val b = g("b", AffineTransform.makeTranslation(2, 2))
        val c = g("c", AffineTransform.makeTranslation(3, 3))
        val d = g("d", AffineTransform.makeTranslation(4, 4))
        val e = g("e", AffineTransform.makeTranslation(5, 5))

        a.children.add(b)
        b.children.add(c)
        c.children.add(d)
        d.children.add(e)
        assertThat(e.ctm).isEqualTo(AffineTransform.makeTranslation(15, 15))

        // Change 'a'
        a.transform = AffineTransform.makeTranslation(10, 10)

        assertThat(e.ctm).isEqualTo(AffineTransform.makeTranslation(24, 24))
    }

    @Test
    fun `re-parenting - moving a subtree should update path`() {
        val root1 = g("root1", AffineTransform.makeTranslation(1, 1))
        val root2 = g("root2", AffineTransform.makeTranslation(10, 10))
        val child = g("child", AffineTransform.makeTranslation(2, 2))

        // Attach to root1
        root1.children.add(child)
        assertThat(child.ctm).isEqualTo(AffineTransform.makeTranslation(3, 3))

        // Move to root2
        root1.children.remove(child)
        root2.children.add(child)

        assertThat(child.ctm).isEqualTo(AffineTransform.makeTranslation(12, 12))
    }
}
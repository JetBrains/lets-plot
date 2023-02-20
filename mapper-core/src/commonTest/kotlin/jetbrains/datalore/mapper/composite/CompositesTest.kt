/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

import jetbrains.datalore.mapper.composite.TestComposite.Companion.create
import kotlin.test.*

class CompositesTest {
    private val root = create(0, 0, 2, 2)
    private val child1 = create(0, 0, 2, 1)
    private val leaf11 = create(0, 0, 1, 1)
    private val leaf12 = create(1, 0, 1, 1)
    private val child2 = create(0, 1, 2, 1)
    private val leaf21 = create(0, 1, 1, 1)
    private val leaf22 = create(1, 1, 1, 1)

    @BeforeTest
    fun init() {
        root.children().addAll(listOf(child1, child2))
        child1.children().addAll(listOf(leaf11, leaf12))
        child2.children().addAll(listOf(leaf21, leaf22))
    }

    @Test
    fun removeFromParent() {
        Composites.removeFromParent(leaf11)
        assertEquals(listOf(leaf12), child1.children())
    }

    @Test
    fun firstLeaf() {
        assertSame(leaf11, Composites.firstLeaf(root))
    }

    @Test
    fun lastLeaf() {
        assertSame(leaf22, Composites.lastLeaf(root))
    }

    @Test
    fun prevSibling() {
        assertNull(Composites.prevSibling(leaf11))
        assertSame(leaf11, Composites.prevSibling(leaf12))
    }

    @Test
    fun nextSibling() {
        assertNull(Composites.nextSibling(leaf12))
        assertSame(leaf12, Composites.nextSibling(leaf11))
    }

    @Test
    fun nextLeafSibling() {
        assertSame(leaf12, Composites.nextLeaf(leaf11))
    }

    @Test
    fun nextLeafNonSibling() {
        assertSame(leaf21, Composites.nextLeaf(leaf12))
    }

    @Test
    fun nextLeafNone() {
        assertNull(Composites.nextLeaf(leaf22))
    }

    @Test
    fun prevLeafSibling() {
        assertSame(leaf11, Composites.prevLeaf(leaf12))
    }

    @Test
    fun prevLeafNonSibling() {
        assertSame(leaf12, Composites.prevLeaf(leaf21))
    }

    @Test
    fun prevLeafNone() {
        assertNull(Composites.prevLeaf(leaf11))
    }

    @Test
    fun nextLeaves() {
        assertEquals(listOf(leaf12, leaf21, leaf22), Composites.nextLeaves(leaf11).toList())
    }

    @Test
    fun prevLeaves() {
        assertEquals(listOf(leaf12, leaf11), Composites.prevLeaves(leaf21).toList())
    }

    @Test
    fun simpleAncestorsFrom() {
        assertEquals(listOf(leaf11, child1, root), Composites.toList(Composites.ancestorsFrom(leaf11)))
    }

    @Test
    fun simpleAncestors() {
        assertEquals(listOf(child1, root), Composites.toList(Composites.ancestors(leaf11)))
    }

    @Test
    fun sameParentIsBefore() {
        assertTrue(Composites.isBefore(leaf11, leaf12))
    }

    @Test
    fun sameParentNotBefore() {
        assertFalse(Composites.isBefore(leaf12, leaf11))
    }

    @Test
    fun differentParentsIsBefore() {
        assertTrue(Composites.isBefore(leaf11, leaf22))
    }

    @Test
    fun itemsNotBeforeItself() {
        assertFalse(Composites.isBefore(leaf11, leaf11))
    }

    @Test
    fun oneAncestorOfOtherInIsBefore() {
        assertFailsWith<IllegalArgumentException> {
            Composites.isBefore(root, leaf11)
        }
    }

    @Test
    fun differentTreesInIsBefore() {
        assertFailsWith<IllegalArgumentException> {
            Composites.isBefore(root, TestComposite())
        }
    }

    @Test
    fun isFirstChildEverythingVisible() {
        assertTrue(Composites.isFirstChild(leaf11))
        assertFalse(Composites.isFirstChild(leaf12))
    }

    @Test
    fun isFirstChildFirstInvisible() {
        leaf11.visible().set(false)

        assertTrue(Composites.isFirstChild(leaf12))
    }

    @Test
    fun isLastChildEverythingVisible() {
        assertFalse(Composites.isLastChild(leaf11))
        assertTrue(Composites.isLastChild(leaf12))
    }

    @Test
    fun isLastChildLastInvisible() {
        assertTrue(Composites.isLastChild(leaf12))
    }

    @Test
    fun firstFocusableSimple() {
        assertSame(leaf11, Composites.firstFocusable(root))
    }

    @Test
    fun firstFocusableFirstLeafNonFocusable() {
        leaf11.focusable().set(false)

        assertSame(leaf12, Composites.firstFocusable(root))
    }

    @Test
    fun firstFocusableFirstLeafInvisible() {
        leaf11.visible().set(false)

        assertSame(leaf12, Composites.firstFocusable(root))
    }

    @Test
    fun lastFocusableSimple() {
        assertSame(leaf22, Composites.lastFocusable(root))
    }

    @Test
    fun lastFocusableLastLeafNonFocusable() {
        leaf22.focusable().set(false)

        assertSame(leaf21, Composites.lastFocusable(root))
    }

    @Test
    fun lastFocusableLastLeafInvisible() {
        leaf22.visible().set(false)

        assertSame(leaf21, Composites.lastFocusable(root))
    }

    @Test
    fun isVisibleInSimpleCase() {
        assertTrue(Composites.isVisible(leaf11))
    }

    @Test
    fun isVisibleItemInvisible() {
        leaf11.visible().set(false)
        assertFalse(Composites.isVisible(leaf11))
    }

    @Test
    fun isVisibleParentInvisible() {
        child1.visible().set(false)
        assertFalse(Composites.isVisible(leaf11))
    }

    @Test
    fun focusableParentSimpleCase() {
        assertSame(child1, Composites.focusableParent(leaf11))
    }

    @Test
    fun focusableParentParentNotFocusable() {
        child1.focusable().set(false)

        assertSame(root, Composites.focusableParent(leaf11))
    }

    @Test
    fun isFocusableSimpleCase() {
        assertTrue(Composites.isFocusable(leaf11))
    }

    @Test
    fun isFocusableParentInvisible() {
        child1.visible().set(false)

        assertFalse(Composites.isFocusable(leaf11))
    }

    @Test
    fun isDescendant() {
        assertTrue(Composites.isDescendant(root, leaf11))
        assertFalse(Composites.isDescendant(leaf11, root))
        assertFalse(Composites.isDescendant(leaf11, leaf12))
    }

    @Test
    fun nestFocusableSimple() {
        assertSame(leaf12, Composites.nextFocusable(leaf11))
    }

    @Test
    fun nextFocusableLeavesNotFocusable() {
        child2.focusable().set(true)
        leaf21.focusable().set(false)
        leaf22.focusable().set(false)

        assertSame(child2, Composites.nextFocusable(leaf12))
    }

    @Test
    fun nestFocusableNoNext() {
        assertNull(Composites.nextFocusable(leaf22))
    }

    @Test
    fun nestFocusableNextUnfocusable() {
        leaf12.focusable().set(false)

        assertSame(leaf21, Composites.nextFocusable(leaf11))
    }

    @Test
    fun prevFocusableSimple() {
        assertSame(leaf11, Composites.prevFocusable(leaf12))
    }

    @Test
    fun prevFocusableLeavesNotFocusable() {
        child1.focusable().set(true)
        leaf11.focusable().set(false)
        leaf12.focusable().set(false)

        assertSame(child1, Composites.prevFocusable(leaf21))
    }

    @Test
    fun prevFocusableNoPrev() {
        assertNull(Composites.prevFocusable(leaf11))
    }

    @Test
    fun prevFocusablePrevUnfocusable() {
        leaf12.focusable().set(false)

        assertSame(leaf11, Composites.prevFocusable(leaf21))
    }

    @Test
    fun nextSatisfying() {
        assertSame(leaf21, Composites.next(leaf12, {
            it.focusable().get()
        }))
    }

    @Test
    fun prevSatisfying() {
        assertSame(leaf12, Composites.prev(leaf21, {
            it.focusable().get()
        }))
    }

    @Test
    fun nextStepsOverNotSatisfying() {
        leaf21.visible().set(false)
        assertSame(leaf22, Composites.next(leaf12, {
            it.visible().get()
        }))
    }

    @Test
    fun prevStepsOverNotSatisfying() {
        leaf21.visible().set(false)
        assertSame(leaf12, Composites.prev(leaf22, {
            it.visible().get()
        }))
    }

    @Test
    fun noNext() {
        assertNull(Composites.next(leaf22, {
            it.visible().get()
        }))
    }

    @Test
    fun noPrev() {
        assertNull(Composites.prev(leaf11, {
            it.visible().get()
        }))
    }

    @Test
    fun isAbove() {
        assertTrue(Composites.isAbove(create(0, 0, 1, 1), create(1, 1, 1, 1)))
        assertFalse(Composites.isAbove(create(0, 0, 1, 1), create(0, 0, 2, 2)))
    }

    @Test
    fun isBelow() {
        assertTrue(Composites.isBelow(create(1, 1, 1, 1), create(0, 0, 1, 1)))
    }

    @Test
    fun homeElement() {
        assertSame(leaf11, Composites.homeElement(leaf12))
    }

    @Test
    fun endElement() {
        assertSame(leaf12, Composites.endElement(leaf11))
    }

    @Test
    fun upperFocusable() {
        assertSame(leaf11, Composites.upperFocusable(leaf22, 0))
        assertSame(leaf12, Composites.upperFocusable(leaf21, 10))
    }

    @Test
    fun lowerFocusable() {
        assertSame(leaf21, Composites.lowerFocusable(leaf12, 0))
        assertSame(leaf22, Composites.lowerFocusable(leaf12, 10))
    }

//    @Test
//    fun iterateBranch() {
//        assertEquals(listOf(root, child1, leaf11, leaf12, child2, leaf21, leaf22),
//                Composites.preOrderTraversal(root))
//    }

    @Test
    fun bothLeftAndRightNull() {
        assertFailsWith<IllegalStateException> {
            Composites.deltaBetween(
                object : TestComposite() {
                    override fun prevSibling(): TestComposite? = null
                },
                object : TestComposite() {
                    override fun nextSibling(): TestComposite? = null
                }
            )
        }
    }
}
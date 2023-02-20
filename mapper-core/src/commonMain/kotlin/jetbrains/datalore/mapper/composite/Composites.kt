/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

import jetbrains.datalore.base.function.Functions
import jetbrains.datalore.base.function.Predicate
import kotlin.math.min

object Composites {
    private val ourWithBounds = CompositesWithBounds(0)

    fun <CompositeT : Composite<CompositeT>> removeFromParent(c: CompositeT) {
        val parent = c.parent ?: return
        parent.children().remove(c)
    }

    fun <CompositeT : NavComposite<CompositeT>> isNonCompositeChild(c: CompositeT): Boolean {
        if (c.parent == null) return false

        if (c.nextSibling() != null) return false
        if (c.prevSibling() != null) return false

        val children = c.parent!!.children()
        if (children.size != 1) return true
        return children[0] !== c

    }

    fun <CompositeT : Composite<CompositeT>> nextSibling(c: CompositeT): CompositeT? {
        val parent = c.parent ?: return null
        val index = parent.children().indexOf(c)
        if (index == -1) return null
        return if (index + 1 < parent.children().size) {
            parent.children()[index + 1]
        } else null
    }

    fun <CompositeT : Composite<CompositeT>> prevSibling(c: CompositeT): CompositeT? {
        val parent = c.parent ?: return null
        val index = parent.children().indexOf(c)
        if (index == -1) return null
        return if (index > 0) {
            parent.children()[index - 1]
        } else null
    }

    fun <CompositeT : NavComposite<CompositeT>> firstLeaf(c: CompositeT): CompositeT {
        val first = c.firstChild() ?: return c
        return firstLeaf(first)
    }

    fun <CompositeT : NavComposite<CompositeT>> lastLeaf(c: CompositeT): CompositeT {
        val last = c.lastChild() ?: return c
        return lastLeaf(last)
    }

    fun <CompositeT : NavComposite<CompositeT>> nextLeaf(c: CompositeT): CompositeT? {
        return nextLeaf(c, null)
    }

    fun <CompositeT : NavComposite<CompositeT>> nextLeaf(c: CompositeT, within: CompositeT?): CompositeT? {
        var current = c
        while (true) {
            val nextSibling = current.nextSibling()
            if (nextSibling != null) {
                return firstLeaf(nextSibling)
            }

            if (isNonCompositeChild(current)) return null
            val parent = current.parent
            if (parent === within) return null
            current = parent!!
        }
    }

    fun <CompositeT : NavComposite<CompositeT>> prevLeaf(c: CompositeT): CompositeT? {
        return prevLeaf(c, null)
    }

    fun <CompositeT : NavComposite<CompositeT>> prevLeaf(c: CompositeT, within: CompositeT?): CompositeT? {
        var current = c
        while (true) {
            val prevSibling = current.prevSibling()
            if (prevSibling != null) {
                return lastLeaf(prevSibling)
            }

            if (isNonCompositeChild(current)) return null

            val parent = current.parent
            if (parent === within) return null
            current = parent!!
        }
    }

    fun <HasParentT : HasParent<HasParentT>> root(current: HasParentT): HasParentT {
        var c = current
        while (true) {
            if (c.parent == null) {
                return c
            }
            c = c.parent!!
        }
    }

    /**
     * @return Iterable containing the current node and all ancestors.
     */
    fun <HasParentT : HasParent<HasParentT>> ancestorsFrom(current: HasParentT): Iterable<HasParentT> {
        return iterateFrom(current, {
            it.parent
        })
    }

    /**
     * @return Iterable containing all ancestors, but not the current node.
     */
    fun <HasParentT : HasParent<HasParentT>> ancestors(current: HasParentT): Iterable<HasParentT> {
        return iterate(current, {
            it.parent
        })
    }

    fun <CompositeT : NavComposite<CompositeT>> nextLeaves(current: CompositeT): Iterable<CompositeT> {
        return iterate(current, {
            nextLeaf(it)
        })
    }

    fun <CompositeT : NavComposite<CompositeT>> prevLeaves(current: CompositeT): Iterable<CompositeT> {
        return iterate(current, {
            prevLeaf(it)
        })
    }

    fun <CompositeT : NavComposite<CompositeT>> nextNavOrder(current: CompositeT): Iterable<CompositeT> {
        return iterate(current, {
            nextNavOrder(current, it)
        })
    }

    fun <CompositeT : NavComposite<CompositeT>> prevNavOrder(current: CompositeT): Iterable<CompositeT> {
        return iterate(current, {
            prevNavOrder(current, it)
        })
    }

    private fun <CompositeT : NavComposite<CompositeT>> nextNavOrder(start: CompositeT, current: CompositeT): CompositeT? {
        val nextSibling = current.nextSibling()

        if (nextSibling != null) {
            return firstLeaf(nextSibling)
        }

        if (isNonCompositeChild(current)) return null

        val parent = current.parent
        return if (!isDescendant(parent, start)) parent else nextNavOrder(start, parent!!)
    }

    private fun <CompositeT : NavComposite<CompositeT>> prevNavOrder(start: CompositeT, current: CompositeT): CompositeT? {
        val prevSibling = current.prevSibling()

        if (prevSibling != null) {
            return lastLeaf(prevSibling)
        }

        if (isNonCompositeChild(current)) return null

        val parent = current.parent
        return if (!isDescendant(parent, start)) parent else prevNavOrder(start, parent!!)
    }

    fun <CompositeT : NavComposite<CompositeT>> isBefore(c1: CompositeT, c2: CompositeT): Boolean {
        if (c1 === c2) return false

        val c1a = reverseAncestors(c1)
        val c2a = reverseAncestors(c2)

        if (c1a[0] !== c2a[0]) {
            throw IllegalArgumentException("Items are in different trees")
        }

        val commonLength = min(c1a.size, c2a.size)
        for (i in 1 until commonLength) {
            val first = c1a[i]
            val second = c2a[i]
            if (first !== second) {
                return deltaBetween(first, second) > 0
            }
        }

        throw IllegalArgumentException("One parameter is an ancestor of the other")
    }

    internal fun <CompositeT : NavComposite<CompositeT>> deltaBetween(c1: CompositeT, c2: CompositeT): Int {
        var left: CompositeT? = c1
        var right: CompositeT? = c1
        var delta = 0

        while (true) {
            if (left === c2) {
                return -delta
            }
            if (right === c2) {
                return delta
            }

            delta++

            if (left == null && right == null) {
                throw IllegalStateException("Both left and right are null")
            }

            if (left != null) {
                left = left.prevSibling()
            }
            if (right != null) {
                right = right.nextSibling()
            }
        }
    }

    /**
     * @return Lowest common ancestor for the given nodes or `null` when they have no common ancestors.
     */
    fun <HasParentT : HasParent<HasParentT>> commonAncestor(object1: HasParentT, object2: HasParentT): HasParentT? {
        when {
            object1 === object2 -> return object1
            isDescendant(object1, object2) -> return object1
            isDescendant(object2, object1) -> return object2
        }

        val stack1 = mutableListOf<HasParentT>()
        val stack2 = mutableListOf<HasParentT>()
        for (c in ancestorsFrom(object1)) {
            stack1.add(c)
        }
        for (c in ancestorsFrom(object2)) {
            stack2.add(c)
        }

        if (stack1.isEmpty() || stack2.isEmpty()) {
            return null
        } else {
            do {
                val pop1 = stack1.removeAt(stack1.size - 1)
                val pop2 = stack2.removeAt(stack2.size - 1)
                if (pop1 !== pop2) {
                    return pop1.parent
                }
            } while (stack1.isNotEmpty() && stack2.isNotEmpty())
            return null
        }
    }

    fun <HasParentT : HasParent<HasParentT>> getClosestAncestor(current: HasParentT,
                                                                acceptSelf: Boolean, p: Predicate<HasParentT>): HasParentT? {
        val ancestors = if (acceptSelf) ancestorsFrom(current) else ancestors(current)
        for (c in ancestors) {
            if (p.invoke(c)) {
                return c
            }
        }
        return null
    }

    fun <HasParentT : HasParent<HasParentT>> isDescendant(ancestor: Any?, current: HasParentT): Boolean {
        return getClosestAncestor(current, true, Functions.same(ancestor)) != null
    }

    private fun <HasParentT : HasParent<HasParentT>> reverseAncestors(c: HasParentT): List<HasParentT> {
        val result = ArrayList<HasParentT>()
        collectReverseAncestors(c, result)
        return result
    }

    private fun <HasParentT : HasParent<HasParentT>> collectReverseAncestors(c: HasParentT, result: MutableList<HasParentT>) {
        val parent = c.parent
        if (parent != null) {
            collectReverseAncestors(parent, result)
        }
        result.add(c)
    }

    internal fun <ItemT> toList(it: Iterable<ItemT>): List<ItemT> {
        val result = ArrayList<ItemT>()
        for (i in it) {
            result.add(i)
        }
        return result
    }

    fun <CompositeT> isLastChild(v: CompositeT): Boolean
            where CompositeT : Composite<CompositeT>, CompositeT : HasVisibility {
        val parent = v.parent ?: return false
        val siblings = parent.children()
        val index = siblings.indexOf(v)
        for (cv in siblings.subList(index + 1, siblings.size)) {
            if (cv.visible().get()) return false
        }
        return true
    }

    fun <CompositeT> isFirstChild(cell: CompositeT): Boolean
            where CompositeT : Composite<CompositeT>, CompositeT : HasVisibility {
        val parent = cell.parent ?: return false
        val siblings = parent.children()
        val index = siblings.indexOf(cell)

        for (cv in siblings.subList(0, index)) {
            if (cv.visible().get()) return false
        }
        return true
    }

    fun <CompositeT> firstFocusable(v: CompositeT): CompositeT?
            where CompositeT : Composite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility {
        return firstFocusable(v, true)
    }

    fun <CompositeT> firstFocusable(v: CompositeT, deepest: Boolean): CompositeT?
            where CompositeT : Composite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility {
        for (cv in v.children()) {
            if (!cv.visible().get()) continue
            if (!deepest && cv.focusable().get()) return cv

            val result = firstFocusable(cv)
            if (null != result) return result
        }

        return if (v.focusable().get()) v else null

    }

    fun <CompositeT> lastFocusable(c: CompositeT): CompositeT?
            where CompositeT : Composite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility {
        return lastFocusable(c, true)
    }

    fun <CompositeT> lastFocusable(v: CompositeT, deepest: Boolean): CompositeT?
            where CompositeT : Composite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility {
        val children = v.children()
        for (i in children.indices.reversed()) {
            val cv = children[i]

            if (!cv.visible().get()) continue
            if (!deepest && cv.focusable().get()) return cv

            val result = lastFocusable(cv, deepest)
            if (null != result) return result
        }

        return if (v.focusable().get()) v else null
    }

    fun <HasParentT> isVisible(v: HasParentT): Boolean
            where HasParentT : HasParent<HasParentT>, HasParentT : HasVisibility {
        return null == getClosestAncestor(v, true) {
            !it.visible().get()
        }
    }

    fun <HasParentT> focusableParent(v: HasParentT): HasParentT?
            where HasParentT : HasParent<HasParentT>, HasParentT : HasFocusability {
        return focusableParent(v, false)
    }

    fun <HasParentT> focusableParent(v: HasParentT, acceptSelf: Boolean): HasParentT?
            where HasParentT : HasParent<HasParentT>, HasParentT : HasFocusability {
        return getClosestAncestor(v, acceptSelf) {
            it.focusable().get()
        }
    }

    fun <HasParentT> isFocusable(v: HasParentT): Boolean
            where HasParentT : HasParent<HasParentT>, HasParentT : HasFocusability, HasParentT : HasVisibility {
        return v.focusable().get() && isVisible(v)
    }

    fun <CompositeT : NavComposite<CompositeT>> next(c: CompositeT, p: Predicate<CompositeT>): CompositeT? {
        for (next in nextNavOrder(c)) {
            if (p.invoke(next)) return next
        }
        return null
    }

    fun <CompositeT : NavComposite<CompositeT>> prev(v: CompositeT, p: Predicate<CompositeT>): CompositeT? {
        for (prev in prevNavOrder(v)) {
            if (p.invoke(prev)) return prev
        }
        return null
    }

    fun <CompositeT> nextFocusable(v: CompositeT): CompositeT?
            where CompositeT : NavComposite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility {
        for (cv in nextNavOrder(v)) {
            if (isFocusable(cv)) return cv
        }
        return null
    }

    fun <CompositeT> prevFocusable(v: CompositeT): CompositeT?
            where CompositeT : NavComposite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility {

        for (cv in prevNavOrder(v)) {
            if (isFocusable(cv)) return cv
        }
        return null
    }

    internal fun <ValueT> iterate(initial: ValueT, trans: (ValueT) -> ValueT?): Iterable<ValueT> {
        return iterateFrom(trans(initial), trans)
    }

    private fun <ValueT> iterateFrom(initial: ValueT?, trans: (ValueT) -> ValueT?): Iterable<ValueT> {
        return object : Iterable<ValueT> {
            override fun iterator(): Iterator<ValueT> {
                return object : Iterator<ValueT> {
                    private var myCurrent: ValueT? = initial

                    override fun hasNext(): Boolean {
                        return myCurrent != null
                    }

                    override fun next(): ValueT {
                        if (myCurrent == null) {
                            throw NoSuchElementException()
                        }
                        val result = myCurrent
                        myCurrent = trans(result!!)
                        return result
                    }
                }
            }
        }
    }

    /**
     * Returns a list that includes all nodes that have some parent strictly between
     * some parents of `from` and `from`.
     */
    fun <CompositeT : NavComposite<CompositeT>> allBetween(from: CompositeT, to: CompositeT): List<CompositeT> {
        val res = ArrayList<CompositeT>()

        if (to !== from) {
            includeClosed(from, to, res)
        }

        return res
    }

    private fun <CompositeT : NavComposite<CompositeT>> includeClosed(left: CompositeT, to: CompositeT, res: MutableList<CompositeT>) {
        var next = left.nextSibling()
        while (next != null) {
            if (includeOpen(next, to, res)) {
                return
            }
            next = next.nextSibling()
        }

        if (left.parent == null) {
            throw IllegalArgumentException("Right bound not found in left's bound hierarchy. to=$to")
        }

        includeClosed(left.parent!!, to, res)
    }

    private fun <CompositeT : NavComposite<CompositeT>> includeOpen(node: CompositeT, to: CompositeT, res: MutableList<CompositeT>): Boolean {

        if (node === to) {
            return true
        }

        for (c in node.children()) {
            if (includeOpen(c, to, res)) {
                return true
            }
        }

        res.add(node)
        return false
    }

    //has bounds
    fun <CompositeT : HasBounds> isAbove(upper: CompositeT, lower: CompositeT): Boolean {
        return ourWithBounds.isAbove(upper, lower)
    }

    fun <CompositeT : HasBounds> isBelow(lower: CompositeT, upper: CompositeT): Boolean {
        return ourWithBounds.isBelow(lower, upper)
    }

    fun <CompositeT> homeElement(cell: CompositeT): CompositeT
            where CompositeT : NavComposite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility, CompositeT : HasBounds {
        return ourWithBounds.homeElement(cell)
    }

    fun <CompositeT> endElement(cell: CompositeT): CompositeT
            where CompositeT : NavComposite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility, CompositeT : HasBounds {
        return ourWithBounds.endElement(cell)
    }

    fun <CompositeT> upperFocusable(v: CompositeT, xOffset: Int): CompositeT?
            where CompositeT : NavComposite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility, CompositeT : HasBounds {
        return ourWithBounds.upperFocusable(v, xOffset)
    }

    fun <CompositeT> lowerFocusable(v: CompositeT, xOffset: Int): CompositeT?
            where CompositeT : NavComposite<CompositeT>, CompositeT : HasFocusability, CompositeT : HasVisibility, CompositeT : HasBounds {
        return ourWithBounds.lowerFocusable(v, xOffset)
    }

    // See also test: CompositesTest#iterateBranch

//    fun <CompositeT : Composite<CompositeT>> preOrderTraversal(root: CompositeT): Iterable<CompositeT> {
//        return Composites.createCompositeTreeTraverser<CompositeT>().preOrderTraversal(root)
//    }
//
//    private fun <CompositeT : Composite<CompositeT>> createCompositeTreeTraverser(): TreeTraverser<CompositeT> {
//        return TreeTraverser.using(object : com.google.common.base.Function<CompositeT, Iterable<CompositeT>>() {
//            fun apply(input: CompositeT?): Iterable<CompositeT> {
//                return if (input == null) emptyList() else input.children()
//            }
//        })
//    }
}
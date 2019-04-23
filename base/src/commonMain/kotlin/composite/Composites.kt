package jetbrains.datalore.base.composite

import jetbrains.datalore.base.function.Predicate

object Composites {
//    private val ourWithBounds = CompositesWithBounds(0)
//
//    fun <CompositeT : Composite<CompositeT>> removeFromParent(c: CompositeT) {
//        val parent = c.getParent() ?: return
//        parent!!.children().remove(c)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> isNonCompositeChild(c: CompositeT): Boolean {
//        if (c.getParent() == null) return false
//
//        if (c.nextSibling() != null) return false
//        if (c.prevSibling() != null) return false
//
//        val children = c.getParent().children()
//        if (children.size != 1) return true
//        return if (children.get(0) !== c) true else false
//
//    }
//
//    fun <CompositeT : Composite<CompositeT>> nextSibling(c: CompositeT): CompositeT? {
//        val parent = c.getParent() ?: return null
//        val index = parent!!.children().indexOf(c)
//        if (index == -1) return null
//        return if (index + 1 < parent!!.children().size()) {
//            parent!!.children().get(index + 1)
//        } else null
//    }
//
//    fun <CompositeT : Composite<CompositeT>> prevSibling(c: CompositeT): CompositeT? {
//        val parent = c.getParent() ?: return null
//        val index = parent!!.children().indexOf(c)
//        if (index == -1) return null
//        return if (index > 0) {
//            parent!!.children().get(index - 1)
//        } else null
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> firstLeaf(c: CompositeT): CompositeT {
//        val first = c.firstChild() ?: return c
//        return firstLeaf(first!!)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> lastLeaf(c: CompositeT): CompositeT {
//        val last = c.lastChild() ?: return c
//        return lastLeaf(last!!)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> nextLeaf(c: CompositeT): CompositeT? {
//        return nextLeaf(c, null)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> nextLeaf(c: CompositeT, within: CompositeT?): CompositeT? {
//        var current = c
//        while (true) {
//            val nextSibling = current.nextSibling()
//            if (nextSibling != null) {
//                return firstLeaf(nextSibling!!)
//            }
//
//            if (isNonCompositeChild(current)) return null
//            val parent = current.getParent()
//            if (parent === within) return null
//            current = parent
//        }
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> prevLeaf(c: CompositeT): CompositeT? {
//        return prevLeaf(c, null)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> prevLeaf(c: CompositeT, within: CompositeT?): CompositeT? {
//        var current = c
//        while (true) {
//            val prevSibling = current.prevSibling()
//            if (prevSibling != null) {
//                return lastLeaf(prevSibling!!)
//            }
//
//            if (isNonCompositeChild(current)) return null
//
//            val parent = current.getParent()
//            if (parent === within) return null
//            current = parent
//        }
//    }

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
        return iterateFrom(current, { hasParent: HasParentT -> hasParent.parent })
    }

    /**
     * @return Iterable containing all ancestors, but not the current node.
     */
    fun <HasParentT : HasParent<HasParentT>> ancestors(current: HasParentT): Iterable<HasParentT> {
        return iterate(current, { hasParent: HasParentT -> hasParent.parent })
    }

//    fun <CompositeT : NavComposite<CompositeT>> nextLeaves(current: CompositeT): Iterable<CompositeT> {
//        return iterate(current, object : Function<CompositeT, CompositeT>() {
//            fun apply(c: CompositeT): CompositeT? {
//                return nextLeaf(c)
//            }
//        })
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> prevLeaves(current: CompositeT): Iterable<CompositeT> {
//        return iterate(current, object : Function<CompositeT, CompositeT>() {
//            fun apply(c: CompositeT): CompositeT? {
//                return prevLeaf(c)
//            }
//        })
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> nextNavOrder(current: CompositeT): Iterable<CompositeT> {
//        return iterate(current, object : Function<CompositeT, CompositeT>() {
//            fun apply(input: CompositeT): CompositeT? {
//                return nextNavOrder(current, input)
//            }
//        })
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> prevNavOrder(current: CompositeT): Iterable<CompositeT> {
//        return iterate(current, object : Function<CompositeT, CompositeT>() {
//            fun apply(input: CompositeT): CompositeT? {
//                return prevNavOrder(current, input)
//            }
//        })
//    }
//
//    private fun <CompositeT : NavComposite<CompositeT>> nextNavOrder(start: CompositeT, current: CompositeT): CompositeT? {
//        val nextSibling = current.nextSibling()
//
//        if (nextSibling != null) {
//            return firstLeaf(nextSibling!!)
//        }
//
//        if (isNonCompositeChild(current)) return null
//
//        val parent = current.getParent()
//        return if (!isDescendant(parent, start)) parent else nextNavOrder(start, parent)
//    }
//
//    private fun <CompositeT : NavComposite<CompositeT>> prevNavOrder(start: CompositeT, current: CompositeT): CompositeT? {
//        val prevSibling = current.prevSibling()
//
//        if (prevSibling != null) {
//            return lastLeaf(prevSibling!!)
//        }
//
//        if (isNonCompositeChild(current)) return null
//
//        val parent = current.getParent()
//        return if (!isDescendant(parent, start)) parent else prevNavOrder(start, parent)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> isBefore(c1: CompositeT, c2: CompositeT): Boolean {
//        if (c1 === c2) return false
//
//        val c1a = reverseAncestors(c1)
//        val c2a = reverseAncestors(c2)
//
//        if (c1a.get(0) !== c2a.get(0)) {
//            throw IllegalArgumentException("Items are in different trees")
//        }
//
//        val commonLength = Math.min(c1a.size, c2a.size)
//        for (i in 1 until commonLength) {
//            val first = c1a.get(i)
//            val second = c2a.get(i)
//            if (first !== second) {
//                return deltaBetween(first, second) > 0
//            }
//        }
//
//        throw IllegalArgumentException("One parameter is an ancestor of the other")
//    }
//
//    internal fun <CompositeT : NavComposite<CompositeT>> deltaBetween(c1: CompositeT, c2: CompositeT): Int {
//        var left: CompositeT? = c1
//        var right: CompositeT? = c1
//        var delta = 0
//
//        while (true) {
//            if (left === c2) {
//                return -delta
//            }
//            if (right === c2) {
//                return delta
//            }
//
//            delta++
//
//            if (left == null && right == null) {
//                throw IllegalStateException("Both left and right are null")
//            }
//
//            if (left != null) {
//                left = left.prevSibling()
//            }
//            if (right != null) {
//                right = right.nextSibling()
//            }
//        }
//    }
//
//    /**
//     * @return Lowest common ancestor for the given nodes or `null` when they have no common ancestors.
//     */
//    fun <HasParentT : HasParent<HasParentT>> commonAncestor(object1: HasParentT, object2: HasParentT): HasParentT? {
//        if (object1 === object2) {
//            return object1
//        } else if (isDescendant(object1, object2)) {
//            return object1
//        } else if (isDescendant(object2, object1)) {
//            return object2
//        }
//
//        val stack1 = Stack<HasParentT>()
//        val stack2 = Stack<HasParentT>()
//        for (c in ancestorsFrom(object1)) {
//            stack1.push(c)
//        }
//        for (c in ancestorsFrom(object2)) {
//            stack2.push(c)
//        }
//
//        if (stack1.isEmpty() || stack2.isEmpty()) {
//            return null
//        } else {
//            do {
//                val pop1 = stack1.pop()
//                val pop2 = stack2.pop()
//                if (pop1 !== pop2) {
//                    return pop1.parent
//                }
//            } while (!stack1.isEmpty() && !stack2.isEmpty())
//            return null
//        }
//    }

    fun <HasParentT : HasParent<HasParentT>> getClosestAncestor(current: HasParentT,
                                                                acceptSelf: Boolean,
                                                                p: Predicate<HasParentT>): HasParentT? {

        val ancestors = if (acceptSelf) ancestorsFrom(current) else ancestors(current)
        for (c in ancestors) {
            if (p(c)) {
                return c
            }
        }
        return null
    }

    fun <HasParentT : HasParent<HasParentT>> isDescendant(ancestor: Any, current: HasParentT): Boolean {
        return getClosestAncestor(current, true, { other: Any -> other === ancestor }) != null
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

//    fun <CompositeT : Composite<CompositeT>> isLastChild(v: CompositeT): Boolean where CompositeT : HasVisibility {
//        val parent = v.getParent() ?: return false
//        val siblings = parent!!.children()
//        val index = siblings.indexOf(v)
//        for (cv in siblings.subList(index + 1, siblings.size)) {
//            if (cv.visible().get()) return false
//        }
//        return true
//    }
//
//    fun <CompositeT : Composite<CompositeT>> isFirstChild(cell: CompositeT): Boolean where CompositeT : HasVisibility {
//        val parent = cell.getParent() ?: return false
//        val siblings = parent!!.children()
//        val index = siblings.indexOf(cell)
//
//        for (cv in siblings.subList(0, index)) {
//            if (cv.visible().get()) return false
//        }
//        return true
//    }
//
//    fun <CompositeT : Composite<CompositeT>> firstFocusable(v: CompositeT): CompositeT? where CompositeT : HasFocusability {
//        return firstFocusable(v, true)
//    }
//
//    fun <CompositeT : Composite<CompositeT>> firstFocusable(v: CompositeT, deepest: Boolean): CompositeT? where CompositeT : HasFocusability {
//        for (cv in v.children()) {
//            if (!cv.visible().get()) continue
//            if (!deepest && cv.focusable().get()) return cv
//
//            val result = firstFocusable(cv)
//            if (result != null) return result
//        }
//
//        return if (v.focusable().get()) v else null
//
//    }
//
//    fun <CompositeT : Composite<CompositeT>> lastFocusable(c: CompositeT): CompositeT? where CompositeT : HasFocusability {
//        return lastFocusable(c, true)
//    }
//
//    fun <CompositeT : Composite<CompositeT>> lastFocusable(v: CompositeT, deepest: Boolean): CompositeT? where CompositeT : HasFocusability {
//        val children = v.children()
//        for (i in children.indices.reversed()) {
//            val cv = children.get(i)
//
//            if (!cv.visible().get()) continue
//            if (!deepest && cv.focusable().get()) return cv
//
//            val result = lastFocusable(cv, deepest)
//            if (result != null) return result
//        }
//
//        return if (v.focusable().get()) v else null
//    }
//
//    fun <HasParentT : HasParent<HasParentT>> isVisible(v: HasParentT): Boolean where HasParentT : HasVisibility {
//        return getClosestAncestor(v, true, object : Predicate<HasParentT>() {
//            fun test(value: HasParentT): Boolean {
//                return !value.visible().get()
//            }
//        }) == null
//    }
//
//    fun <HasParentT : HasParent<HasParentT>> focusableParent(v: HasParentT): HasParentT? where HasParentT : HasFocusability {
//        return focusableParent(v, false)
//    }
//
//    fun <HasParentT : HasParent<HasParentT>> focusableParent(v: HasParentT, acceptSelf: Boolean): HasParentT? where HasParentT : HasFocusability {
//        return getClosestAncestor(v, acceptSelf, object : Predicate<HasParentT>() {
//            fun test(value: HasParentT): Boolean {
//                return value.focusable().get()
//            }
//        })
//    }
//
//    fun <HasParentT : HasParent<HasParentT>> isFocusable(v: HasParentT): Boolean where HasParentT : HasFocusability {
//        return v.focusable().get() && isVisible(v)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> next(c: CompositeT, p: Predicate<CompositeT>): CompositeT? {
//        for (next in nextNavOrder(c)) {
//            if (p.test(next)) return next
//        }
//        return null
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> prev(v: CompositeT, p: Predicate<CompositeT>): CompositeT? {
//        for (prev in prevNavOrder(v)) {
//            if (p.test(prev)) return prev
//        }
//        return null
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> nextFocusable(v: CompositeT): CompositeT? where CompositeT : HasFocusability {
//        for (cv in nextNavOrder(v)) {
//            if (isFocusable(cv)) return cv
//        }
//        return null
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> prevFocusable(v: CompositeT): CompositeT? where CompositeT : HasFocusability {
//        for (cv in prevNavOrder(v)) {
//            if (isFocusable(cv)) return cv
//        }
//        return null
//    }

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
                        val result = myCurrent!!
                        myCurrent = trans(myCurrent!!)
                        return result
                    }
                }
            }
        }
    }

//    /**
//     * Returns a list that includes all nodes that have some parent strictly between
//     * some parents of `from` and `from`.
//     */
//    fun <CompositeT : NavComposite<CompositeT>> allBetween(from: CompositeT, to: CompositeT): List<CompositeT> {
//        val res = ArrayList<CompositeT>()
//
//        if (to !== from) {
//            includeClosed(from, to, res)
//        }
//
//        return res
//    }
//
//    private fun <CompositeT : NavComposite<CompositeT>> includeClosed(left: CompositeT, to: CompositeT, res: MutableList<CompositeT>) {
//        var next = left.nextSibling()
//        while (next != null) {
//            if (includeOpen(next, to, res)) {
//                return
//            }
//            next = next!!.nextSibling()
//        }
//
//        if (left.getParent() == null) {
//            throw IllegalArgumentException("Right bound not found in left's bound hierarchy. to=$to")
//        }
//
//        includeClosed(left.getParent(), to, res)
//    }
//
//    private fun <CompositeT : NavComposite<CompositeT>> includeOpen(node: CompositeT, to: CompositeT, res: MutableList<CompositeT>): Boolean {
//
//        if (node === to) {
//            return true
//        }
//
//        for (c in node.children()) {
//            if (includeOpen(c, to, res)) {
//                return true
//            }
//        }
//
//        res.add(node)
//        return false
//    }
//
//    //has bounds
//    fun <CompositeT : HasBounds> isAbove(upper: CompositeT, lower: CompositeT): Boolean {
//        return ourWithBounds.isAbove(upper, lower)
//    }
//
//    fun <CompositeT : HasBounds> isBelow(lower: CompositeT, upper: CompositeT): Boolean {
//        return ourWithBounds.isBelow(lower, upper)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> homeElement(cell: CompositeT): CompositeT where CompositeT : HasFocusability {
//        return ourWithBounds.homeElement(cell)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> endElement(cell: CompositeT): CompositeT where CompositeT : HasFocusability {
//        return ourWithBounds.endElement(cell)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> upperFocusable(v: CompositeT, xOffset: Int): CompositeT where CompositeT : HasFocusability {
//        return ourWithBounds.upperFocusable(v, xOffset)
//    }
//
//    fun <CompositeT : NavComposite<CompositeT>> lowerFocusable(v: CompositeT, xOffset: Int): CompositeT where CompositeT : HasFocusability {
//        return ourWithBounds.lowerFocusable(v, xOffset)
//    }
//
//    fun <CompositeT : Composite<CompositeT>> preOrderTraversal(root: CompositeT): Iterable<CompositeT> {
//        return Composites.createCompositeTreeTraverser<CompositeT>().depthFirstPreOrder(root)
//    }
//
//    private fun <CompositeT : Composite<CompositeT>> createCompositeTreeTraverser(): Traverser<CompositeT> {
//        return Traverser.forTree(
//                object : SuccessorsFunction<CompositeT>() {
//                    fun successors(node: CompositeT?): Iterable<CompositeT> {
//                        return if (node == null) emptyList() else node.children()
//                    }
//                }
//        )
//    }
}

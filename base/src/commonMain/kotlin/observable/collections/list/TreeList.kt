package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.DataloreIndexOutOfBoundsException
import kotlin.math.abs
import kotlin.math.max

class TreeList<T> : AbstractMutableList<T>() {
    private var myTree: AvlTree<T>? = null

    override val size: Int
        get() = myTree?.mySize ?: 0


    override fun get(index: Int): T {
        return myTree?.get(index) ?: throw DataloreIndexOutOfBoundsException(index)
    }


    override fun set(index: Int, element: T): T {
        if (myTree == null) {
            throw DataloreIndexOutOfBoundsException(index)
        }
        val oldValue = myTree!![index]
        myTree = myTree!!.set(index, element)
        return oldValue
    }

    override fun add(index: Int, element: T) {
        myTree =
                if (myTree == null) {
                    if (index != 0) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    AvlTree(element)
                } else {
                    myTree!!.insert(index, element)
                }
    }

    override fun removeAt(index: Int): T {
        if (myTree == null) {
            throw DataloreIndexOutOfBoundsException(index)
        }
        val oldValue = myTree!![index]
        myTree = myTree!!.remove(index)
        return oldValue
    }

    fun check() {
        if (myTree == null) return
        myTree!!.check()
    }

    private class AvlTree<T>(private val myLeft: AvlTree<T>?, private val myRight: AvlTree<T>?, private val myValue: T) {
        private val myHeight: Int
        internal val mySize: Int

        internal constructor(value: T) : this(null, null, value)

        init {
            myHeight = max(height(myLeft), height(myRight)) + 1
            mySize = 1 + size(myLeft) + size(myRight)
        }

        internal fun rotateRight(): AvlTree<T> {
            if (myLeft == null) {
                throw IllegalStateException()
            }

            return AvlTree(myLeft.myLeft, AvlTree(myLeft.myRight, myRight, myValue), myLeft.myValue)
        }

        internal fun rotateLeft(): AvlTree<T> {
            if (myRight == null) {
                throw IllegalStateException()
            }

            return AvlTree(AvlTree(myLeft, myRight.myLeft, myValue), myRight.myRight, myRight.myValue)
        }

        internal operator fun get(index: Int): T {
            val leftSize = size(myLeft)

            return when {
                index < leftSize -> {
                    if (myLeft == null) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    myLeft[index]
                }
                index == leftSize -> myValue
                else -> {
                    if (myRight == null) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    myRight[index - leftSize - 1]
                }
            }
        }

        internal operator fun set(index: Int, value: T): AvlTree<T> {
            val leftSize = size(myLeft)

            return when {
                index < leftSize -> {
                    if (myLeft == null) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    AvlTree(myLeft.set(index, value), myRight, myValue)
                }
                index == leftSize -> AvlTree(myLeft, myRight, value)
                else -> {
                    if (myRight == null) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    AvlTree(myLeft, myRight.set(index - 1 - leftSize, value), myValue)
                }
            }
        }


        internal fun insert(index: Int, value: T): AvlTree<T> {
            val leftSize = size(myLeft)
            if (index <= leftSize) {
                val unbalanced: AvlTree<T> =
                        if (myLeft == null) {
                            if (index == 0) {
                                AvlTree(AvlTree(value), myRight, myValue)
                            } else {
                                throw DataloreIndexOutOfBoundsException(index)
                            }
                        } else {
                            AvlTree(myLeft.insert(index, value), myRight, myValue)
                        }
                return balanceLeft(unbalanced)
            } else {
                val unbalanced: AvlTree<T> =
                        if (myRight == null) {
                            if (index == leftSize + 1) {
                                AvlTree(myLeft, AvlTree(value), myValue)
                            } else {
                                throw DataloreIndexOutOfBoundsException(index)
                            }
                        } else {
                            AvlTree(myLeft, myRight.insert(index - 1 - leftSize, value), myValue)
                        }
                return balanceRight(unbalanced)
            }
        }

        private fun balanceRight(result: AvlTree<T>): AvlTree<T> {
            val delta = height(result.myRight) - height(result.myLeft)
            return if (delta > 1) {
                if (height(result.myRight!!.myLeft) <= height(result.myRight.myRight)) {
                    result.rotateLeft()
                } else {
                    AvlTree(result.myLeft, result.myRight.rotateRight(), result.myValue).rotateLeft()
                }
            } else result
        }

        private fun balanceLeft(result: AvlTree<T>): AvlTree<T> {
            val delta = height(result.myLeft) - height(result.myRight)
            return if (delta > 1) {
                if (height(result.myLeft!!.myLeft) >= height(result.myLeft.myRight)) {
                    result.rotateRight()
                } else {
                    AvlTree(result.myLeft.rotateLeft(), result.myRight, result.myValue).rotateRight()
                }
            } else result

        }

        internal fun remove(index: Int): AvlTree<T>? {
            val leftSize = size(myLeft)

            when {
                index == leftSize -> {
                    if (myLeft == null) return myRight
                    if (myRight == null) return myLeft

                    val newVal = myRight[0]
                    return balanceLeft(AvlTree(myLeft, myRight.remove(0), newVal))
                }
                index < leftSize -> {
                    if (myLeft == null) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    return balanceRight(AvlTree(myLeft.remove(index), myRight, myValue))
                }
                else -> {
                    if (myRight == null) {
                        throw DataloreIndexOutOfBoundsException(index)
                    }
                    return balanceLeft(AvlTree(myLeft, myRight.remove(index - leftSize - 1), myValue))
                }
            }
        }

        internal fun check() {
            if (abs(height(myLeft) - height(myRight)) > 1) {
                throw IllegalStateException()
            }
            myLeft?.check()
            myRight?.check()
        }

        private fun height(tree: AvlTree<*>?): Int {
            return tree?.myHeight ?: 0
        }

        private fun size(tree: AvlTree<*>?): Int {
            return tree?.mySize ?: 0
        }

        override fun hashCode(): Int {
            var result = 0
            if (myLeft != null) {
                result += 31 * myLeft.hashCode()
            }
            if (myRight != null) {
                result += 71 * myRight.hashCode()
            }

            result += myValue?.hashCode() ?: 0
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (other !is AvlTree<*>) {
                return false
            }

            val otherTree = other as AvlTree<*>?

            return (otherTree!!.myValue === myValue
                    && otherTree!!.myLeft == myLeft
                    && otherTree.myRight == myRight)
        }
    }
}
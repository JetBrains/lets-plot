/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework


internal class DifferenceBuilder<ItemT>(
        private val mySourceList: List<ItemT>,
        private val myTargetList: List<ItemT>) {

    fun build(): List<DifferenceItem> {
        val result = ArrayList<DifferenceItem>()

        val sourceContent = mySourceList
        val target = ArrayList(myTargetList)

        for (i in target.indices.reversed()) {
            val current = target[i]
            if (!sourceContent.contains(current)) {
                result.add(DifferenceItem(i, current, false))
                target.removeAt(i)
            }
        }

        for (i in sourceContent.indices) {
            val current = sourceContent[i]
            var next: ItemT? = null
            if (i + 1 < sourceContent.size) {
                next = sourceContent[i + 1]
            }

            if (target.size <= i) {
                result.add(DifferenceItem(i, current, true))
                target.add(i, current)
            } else {
                val currentTarget = target[i]
                if (currentTarget !== current) {
                    val currentIndex = target.indexOf(current)
                    if (currentIndex != -1) {
                        result.add(DifferenceItem(currentIndex, current, false))
                        target.remove(current)
                    }

                    if (next === currentTarget) {
                        result.add(DifferenceItem(i, current, true))
                        target.add(i, current)
                    } else {
                        result.add(DifferenceItem(i, currentTarget, false))
                        result.add(DifferenceItem(i, current, true))
                        target[i] = current
                    }
                }
            }
        }

        return result
    }

    internal inner class DifferenceItem(
            val index: Int,
            val item: ItemT,
            val isAdd: Boolean) {

        fun apply(items: MutableList<ItemT>) {
            if (isAdd) {
                items.add(index, item)
            } else {
                items.removeAt(index)
            }
        }

        override fun toString(): String {
            return (if (isAdd) "add" else "remove") + " " + item + "@" + index
        }
    }
}

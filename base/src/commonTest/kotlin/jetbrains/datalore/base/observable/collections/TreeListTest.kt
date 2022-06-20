/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections

import jetbrains.datalore.base.observable.collections.list.TreeList
import kotlin.random.Random
import kotlin.test.Test

class TreeListTest {
    @Test
    fun testTreeList() {
        val random = Random(239)
        val maxValue = 1000

        for (i in 0..29) {
            val arrayList = ArrayList<Int>()
            val treeList = TreeList<Int>()

            for (j in 0..149) {
                val op = random.nextInt(3)

                if (op == 0) {
                    val index = random.nextInt(arrayList.size + 1)
                    val element = random.nextInt(maxValue)

                    arrayList.add(index, element)
                    treeList.add(index, element)
                } else if (op == 1 && !arrayList.isEmpty()) {
                    val index = random.nextInt(arrayList.size)
                    val element = random.nextInt(maxValue)

                    arrayList[index] = element
                    treeList[index] = element
                } else if (op == 2 && !arrayList.isEmpty()) {
                    val index = random.nextInt(arrayList.size)

                    arrayList.removeAt(index)
                    treeList.removeAt(index)
                }

                treeList.check()
                if (arrayList != treeList) {
                    throw IllegalStateException()
                }
            }
        }
    }
}
/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.gcommon.collect

class Stack<T> {
    private val elements: MutableList<T> = ArrayList()

    fun empty() = elements.isEmpty()

    fun push(item: T) = elements.add(item)

    fun pop(): T? = if (elements.isEmpty()) null else elements.removeAt(elements.size - 1)

    fun peek(): T? = elements.lastOrNull()
}

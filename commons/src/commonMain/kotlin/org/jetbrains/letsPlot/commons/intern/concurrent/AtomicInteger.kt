/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.concurrent

expect class AtomicInteger(initialValue: Int) {
    fun decrementAndGet(): Int
    fun incrementAndGet(): Int
}
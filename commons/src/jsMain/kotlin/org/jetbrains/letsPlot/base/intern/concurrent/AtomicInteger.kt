/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.concurrent

actual class AtomicInteger actual constructor(int: Int) {
    private var value: Int = int
    actual fun decrementAndGet(): Int {
        return --value
    }
}
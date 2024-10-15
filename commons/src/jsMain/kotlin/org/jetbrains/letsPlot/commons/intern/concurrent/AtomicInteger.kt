/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.concurrent

actual class AtomicInteger actual constructor(initialValue: Int) {
    private var value: Int = initialValue
    actual fun decrementAndGet(): Int {
        return --value
    }

    actual fun incrementAndGet(): Int {
        return ++value
    }
}
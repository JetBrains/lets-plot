/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.concurrent

/**
 * We don't use it in 'native' code.
 */
actual class AtomicInteger actual constructor(int: Int) {
    actual fun decrementAndGet(): Int {
        throw IllegalStateException("'AtomicInteger' is not supported in any 'native' target.")
    }
}
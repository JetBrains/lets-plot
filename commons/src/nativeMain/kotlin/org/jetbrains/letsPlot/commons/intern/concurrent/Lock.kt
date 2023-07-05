/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.concurrent

/**
 * We don't use it in 'native' code.
 */
actual class Lock actual constructor() {
    actual fun lock() {
        throw IllegalStateException("'Lock' is not supported in any 'native' target.")
    }

    actual fun unlock() {
        throw IllegalStateException("'Lock' is not supported in any 'native' target.")
    }
}
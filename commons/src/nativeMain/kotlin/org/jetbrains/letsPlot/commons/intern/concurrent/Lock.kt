/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.concurrent

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock

actual class Lock actual constructor() {
    private val delegate: ReentrantLock = reentrantLock()

    actual fun lock() {
        delegate.lock()
    }

    actual fun unlock() {
        delegate.unlock()
    }
}

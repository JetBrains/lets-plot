/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.concurrent

import java.util.concurrent.locks.ReentrantLock

actual class Lock actual constructor() {
    private val mutex = ReentrantLock()

    actual fun lock() = mutex.lock()
    actual fun unlock()  = mutex.unlock()
}
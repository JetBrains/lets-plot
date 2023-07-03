/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.concurrent

expect class Lock() {
    fun lock()
    fun unlock()
}

inline fun <R> Lock.execute(f: () -> R): R {
    try {
        lock()
        return f()
    } finally {
        unlock()
    }
}

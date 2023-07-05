/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.concurrent

import java.util.concurrent.atomic.AtomicInteger

actual class AtomicInteger actual constructor(int: Int) {
    private val value: AtomicInteger = AtomicInteger(int)

    actual fun decrementAndGet(): Int {
        return value.decrementAndGet()
    }
}
/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.concurrent

import java.util.concurrent.atomic.AtomicInteger

actual class AtomicInteger actual constructor(int: Int) {
    private val value: AtomicInteger = AtomicInteger(int)

    actual fun decrementAndGet(): Int {
        return value.decrementAndGet()
    }
}
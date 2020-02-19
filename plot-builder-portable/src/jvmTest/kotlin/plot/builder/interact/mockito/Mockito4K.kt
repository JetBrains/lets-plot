/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact.mockito

import org.mockito.Mockito

fun <T> eq(value: T): T {
    Mockito.eq<T>(value)
    return uninitialized()
}

private fun <T> uninitialized(): T = null as T


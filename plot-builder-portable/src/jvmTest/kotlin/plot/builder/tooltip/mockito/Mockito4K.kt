/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.mockito

import org.mockito.Mockito

fun <T> eq(value: T): T {
    Mockito.eq<T>(value)
    return uninitialized()
}

@Suppress("UNCHECKED_CAST")
private fun <T> uninitialized() = null as T


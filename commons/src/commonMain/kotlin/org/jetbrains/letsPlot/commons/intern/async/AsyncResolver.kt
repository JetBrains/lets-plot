/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.async

internal interface AsyncResolver<ItemT> {
    fun success(result: ItemT)

    fun failure(t: Throwable)
}
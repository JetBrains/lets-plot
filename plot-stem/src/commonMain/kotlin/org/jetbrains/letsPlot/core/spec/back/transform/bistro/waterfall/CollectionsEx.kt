/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transform.bistro.waterfall

fun <T, R : Comparable<R>> Iterable<T>.sortedIndicesDescending(selector: (IndexedValue<T>) -> R?) =
    withIndex().sortedWith(compareByDescending(selector)).map(IndexedValue<T>::index)
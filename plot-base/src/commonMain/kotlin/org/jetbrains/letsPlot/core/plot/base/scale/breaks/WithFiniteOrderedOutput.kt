/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

interface WithFiniteOrderedOutput<T> {
    val outputValues: List<T>

    fun getOutputValueIndex(domainValue: Any): Int

    fun getOutputValue(domainValue: Any): T?
}

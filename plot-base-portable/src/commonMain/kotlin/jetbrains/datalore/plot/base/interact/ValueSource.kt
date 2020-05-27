/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes

interface ValueSource {

    fun setDataPointProvider(dataContext: DataContext)

    fun getDataPoint(index: Int): DataPoint?

    class DataPoint(
        val label: String,
        val value: String,
        val isContinuous: Boolean,
        val aes: Aes<*>?,
        val isAxis: Boolean,
        val isOutlier: Boolean
    ) {
        val line: String
            get() = if (label.isNotEmpty()) "$label: $value" else value
    }
}
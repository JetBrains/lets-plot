/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.base.interact.ValueSource.DataPoint

class StaticValue(private val text: String) : ValueSource {

    override fun setDataPointProvider(dataContext: DataContext) {
    }

    override fun getDataPoint(index: Int): DataPoint? {
        return DataPoint(
            label = "",
            value = text,
            isContinuous = false,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }
}

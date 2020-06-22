/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.ValueSource

open class ConstantValue(
    private val label: String? = null,
    value: Any,
    format: String? = null
) : ValueSource {

    private var myIsContinuous: Boolean = value is Number
    private val myDataValue = LineFormatter(format).format(value.toString(), myIsContinuous)

    override fun setDataContext(dataContext: DataContext) {
    }

    override fun getDataPoint(index: Int): ValueSource.DataPoint? {
        return ValueSource.DataPoint(
            label = label ?: "",
            value = myDataValue,
            isContinuous = myIsContinuous,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }
}
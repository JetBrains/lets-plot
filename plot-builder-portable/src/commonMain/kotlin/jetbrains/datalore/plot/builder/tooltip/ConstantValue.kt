/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class ConstantValue(
    value: Any,
    format: String? = null
) : ValueSource {

    private val myDataValue = if (format != null) {
        StringFormat(format).format(value)
    } else {
        value.toString()
    }

    override fun setDataContext(dataContext: DataContext) {
    }

    override fun getDataPoint(index: Int): DataPoint? {
        return DataPoint(
            label = "",
            value = myDataValue,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    override fun copy(): ValueSource {
        return this
    }
}
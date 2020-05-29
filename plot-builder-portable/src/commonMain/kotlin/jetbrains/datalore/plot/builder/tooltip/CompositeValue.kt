/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.base.interact.ValueSource.DataPoint

class CompositeValue(
    private val values: List<ValueSource>,
    private val label: String,
    format: String
) : ValueSource {

    private val myFormatter = if (format.isEmpty()) null else LineFormatter(format)

    override fun setDataPointProvider(dataContext: DataContext) {
        values.forEach { it.setDataPointProvider(dataContext) }
    }

    override fun getDataPoint(index: Int): DataPoint? {
        val dataValues = values.map { dataValue ->
            dataValue.getDataPoint(index) ?: return null
        }
        return DataPoint(
            label = label,
            value = combine(dataValues),
            isContinuous = false,
            aes = null,
            isAxis = false,
            isOutlier = false
        )
    }

    private fun combine(dataValuePoints: List<DataPoint>): String {
        return myFormatter?.format(dataValuePoints)
            ?: dataValuePoints.joinToString(transform = DataPoint::line)
    }
}
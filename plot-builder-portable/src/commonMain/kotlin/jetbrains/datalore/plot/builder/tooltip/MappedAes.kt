/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.ValueSource
import jetbrains.datalore.plot.base.interact.ValueSource.DataPoint
import jetbrains.datalore.plot.builder.map.GeoPositionField

open class MappedAes(
    protected val aes: Aes<*>,
    private val isOutlier: Boolean = false,
    private val isAxis: Boolean = false
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess

    override fun setDataPointProvider(dataContext: DataContext) {
        myDataAccess = dataContext.mappedDataAccess
    }

    override fun getDataPoint(index: Int): DataPoint? {
        val mappedDataPoint = getMappedDataPoint(index) ?: return null

        val label2value: Pair<String, String> = when {
            isAxis && !isAxisTooltipAllowed(mappedDataPoint) -> null
            isAxis -> "" to mappedDataPoint.value
            else -> createLabeledValue(
                index = index,
                value = mappedDataPoint.value,
                label = mappedDataPoint.label
            )
        } ?: return null

        return DataPoint(
            label = label2value.first,
            value = label2value.second,
            isContinuous = mappedDataPoint.isContinuous,
            aes = mappedDataPoint.aes,
            isAxis = mappedDataPoint.isAxis,
            isOutlier = mappedDataPoint.isOutlier
        )
    }

    protected open fun getMappedDataPoint(index: Int): DataPoint? {
        if (!myDataAccess.isMapped(aes)) {
            return null
        }
        val mappedData = myDataAccess.getMappedData(aes, index)
        return DataPoint(
            label = mappedData.label,
            value = mappedData.value,
            isContinuous = mappedData.isContinuous,
            aes = aes,
            isAxis = isAxis,
            isOutlier = isOutlier
        )
    }

    private fun isAxisTooltipAllowed(sourceDataPoint: DataPoint): Boolean {
        return when {
            MAP_COORDINATE_NAMES.contains(sourceDataPoint.label) -> false
            else -> sourceDataPoint.isContinuous
        }
    }

    private fun createLabeledValue(
        index: Int,
        value: String,
        label: String
    ): Pair<String, String> {

        val axisLabels = listOf(Aes.X, Aes.Y).mapNotNull { axisAes ->
            if (myDataAccess.isMapped(axisAes)) {
                val mappedData = myDataAccess.getMappedData(axisAes, index)
                mappedData.label
            } else {
                null
            }
        }

        fun shortText() = "" to value

        fun fullText() = label to value

        return when {
            label.isEmpty() -> shortText()
            label in axisLabels -> shortText()
            else -> fullText()
        }
    }

    /* For tests only */
    fun getAesName(): String {
        return aes.name
    }

    companion object {
        private val MAP_COORDINATE_NAMES = setOf(
            GeoPositionField.POINT_X,
            GeoPositionField.POINT_X1,
            GeoPositionField.POINT_Y,
            GeoPositionField.POINT_Y1
        )

        fun createMappedAxis(aes: Aes<*>, dataContext: DataContext): ValueSource =
            MappedAes(aes, isOutlier = true, isAxis = true).also { it.setDataPointProvider(dataContext) }

        fun createMappedAes(aes: Aes<*>, isOutlier: Boolean, dataContext: DataContext): ValueSource =
            MappedAes(aes, isOutlier = isOutlier, isAxis = false).also { it.setDataPointProvider(dataContext) }
    }
}

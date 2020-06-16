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

open class MappedAes(
    protected val aes: Aes<*>,
    private val isOutlier: Boolean = false,
    private val isAxis: Boolean = false,
    private val label: String? = null,
    format: String? = null
) : ValueSource {

    private var myIsTooltipAllowed: Boolean = false
    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var myDataLabel: String
    private var myIsContinuous: Boolean = false
    private val myFormatter = LineFormatter(format)

    override fun setDataContext(dataContext: DataContext) {
        myDataAccess = dataContext.mappedDataAccess

        require(myDataAccess.isMapped(aes)) { "$aes have to be mapped" }

        val axisLabels = listOf(Aes.X, Aes.Y)
            .filter(myDataAccess::isMapped)
            .map(myDataAccess::getMappedDataLabel)
        val dataLabel = myDataAccess.getMappedDataLabel(aes)
        myDataLabel = when {
            isAxis -> ""
            dataLabel.isEmpty() -> ""
            dataLabel in axisLabels -> ""
            else -> dataLabel
        }
        myIsContinuous = myDataAccess.isMappedDataContinuous(aes)
        myIsTooltipAllowed = when {
            !isAxis -> true
            MAP_COORDINATE_NAMES.contains(dataLabel) -> false
            else -> myIsContinuous
        }
    }

    override fun getDataPoint(index: Int): DataPoint? {
        return if (!myIsTooltipAllowed) {
            null
        } else {
            val mappedDataValue = myDataAccess.getMappedData(aes, index).value
            DataPoint(
                label = label ?: myDataLabel,
                value = myFormatter.format(mappedDataValue, myIsContinuous),
                isContinuous = myIsContinuous,
                aes = aes,
                isAxis = isAxis,
                isOutlier = isOutlier
            )
        }
    }

    /* For tests only */
    fun getAesName(): String {
        return aes.name
    }

    companion object {
        fun createMappedAxis(aes: Aes<*>, dataContext: DataContext): ValueSource =
            MappedAes(aes, isOutlier = true, isAxis = true).also { it.setDataContext(dataContext) }

        fun createMappedAes(aes: Aes<*>, isOutlier: Boolean, dataContext: DataContext): ValueSource =
            MappedAes(aes, isOutlier = isOutlier, isAxis = false).also { it.setDataContext(dataContext) }
    }
}

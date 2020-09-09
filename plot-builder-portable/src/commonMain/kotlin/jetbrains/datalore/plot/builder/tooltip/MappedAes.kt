/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.numberFormat.NumberFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class MappedAes(
    val aes: Aes<*>,
    private val isOutlier: Boolean = false,
    private val isAxis: Boolean = false,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var myDataLabel: String
    private var myIsContinuous: Boolean = false
    private val myFormatter: NumberFormat? = format?.let { NumberFormat(it) }

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
        if (myFormatter != null) {
            require(myIsContinuous) { "Wrong format pattern: numeric for non-numeric value" }
        }
    }

    override fun getDataPoint(index: Int): DataPoint? {
        return if (isAxis && !myIsContinuous) {
            null
        } else {
            val mappedDataValue = myDataAccess.getMappedData(aes, index).value
            DataPoint(
                label = myDataLabel,
                value = ValueSource.formatValueSource(mappedDataValue, myFormatter),
                isContinuous = myIsContinuous,
                aes = aes,
                isAxis = isAxis,
                isOutlier = isOutlier
            )
        }
    }

    fun toOutlier() : MappedAes{
        return MappedAes(
            aes = aes,
            isOutlier = true,
            isAxis = isAxis,
            format = format
        )
    }
}

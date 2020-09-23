/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint
import jetbrains.datalore.plot.base.util.StringFormat

class MappingValue(
    val aes: Aes<*>,
    private val isOutlier: Boolean = false,
    private val isAxis: Boolean = false,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var myDataLabel: String
    private var myIsContinuous: Boolean = false
    private val myFormatter = format?.let { StringFormat(it) }

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
    }

    override fun getDataPoint(index: Int): DataPoint? {
        return if (isAxis && !myIsContinuous) {
            null
        } else {
            val originalValue = myDataAccess.getOriginalValue(aes, index)
            val formattedValue =
                originalValue?.let { myFormatter?.format(it) } ?: myDataAccess.getMappedData(aes, index).value

            // for outliers: myDataLabel is a part of the value, but pattern format removes this part
            val value = if (isOutlier && myDataLabel.isNotEmpty() &&
                myFormatter?.formatType != StringFormat.FormatType.STRING_FORMAT
            ) {
                "$myDataLabel: $formattedValue"
            } else {
                formattedValue
            }
            DataPoint(
                label = if (isOutlier) "" else myDataLabel,
                value = value,
                isContinuous = myIsContinuous,
                aes = aes,
                isAxis = isAxis,
                isOutlier = isOutlier
            )
        }
    }

    fun toOutlier(): MappingValue {
        return MappingValue(
            aes = aes,
            isOutlier = true,
            isAxis = isAxis,
            format = format
        )
    }
}

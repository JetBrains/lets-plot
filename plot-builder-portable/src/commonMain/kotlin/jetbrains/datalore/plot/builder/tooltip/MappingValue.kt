/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.TooltipLineSpec.DataPoint

class MappingValue(
    val aes: Aes<*>,
    override val isOutlier: Boolean = false,
    override val isAxis: Boolean = false,
    private val format: String? = null
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private var myDataLabel: String? = null
    private val myFormatter = format?.let {
        StringFormat.forOneArg(format, formatFor = aes.name)
    }

    override fun initDataContext(dataContext: DataContext) {
        require(!::myDataAccess.isInitialized) { "Data context can be initialized only once" }
        myDataAccess = dataContext.mappedDataAccess

        require(myDataAccess.isMapped(aes)) { "$aes have to be mapped" }

        val axisLabels = listOf(Aes.X, Aes.Y)
            .filter(myDataAccess::isMapped)
            .map(myDataAccess::getMappedDataLabel)
        val dataLabel = myDataAccess.getMappedDataLabel(aes)
        myDataLabel = when {
            isAxis -> null
            isOutlier -> null
            dataLabel.isEmpty() -> ""
            dataLabel in axisLabels -> ""
            else -> dataLabel
        }
    }

    override fun getDataPoint(index: Int): DataPoint {
        val originalValue = myDataAccess.getOriginalValue(aes, index)
        val formattedValue =
            originalValue?.let { myFormatter?.format(it) } ?: myDataAccess.getMappedDataValue(aes, index)
        return DataPoint(
            label = myDataLabel,
            value = formattedValue,
            aes = aes,
            isAxis = isAxis,
            isOutlier = isOutlier
        )
    }

    override fun copy(): MappingValue {
        return MappingValue(
            aes = aes,
            isOutlier = isOutlier,
            isAxis = isAxis,
            format = format
        )
    }

    fun withFlags(isOutlier: Boolean, isAxis: Boolean): MappingValue {
        return MappingValue(
            aes = aes,
            isOutlier = isOutlier,
            isAxis = isAxis,
            format = format
        )
    }
}

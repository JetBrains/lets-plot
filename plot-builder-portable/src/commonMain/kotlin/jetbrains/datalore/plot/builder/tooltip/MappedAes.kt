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

    private var myIsTooltipAllowed: Boolean = false
    private lateinit var myDataAccess: MappedDataAccess
    private lateinit var myLabel: String
    protected var myIsContinuous: Boolean = false

    override fun setDataContext(dataContext: DataContext) {
        myDataAccess = dataContext.mappedDataAccess

        require(myDataAccess.isMapped(aes)) { "$aes have to be mapped" }

        myLabel = initLabel()
        myIsContinuous = myDataAccess.isMappedDataContinuous(aes)
        myIsTooltipAllowed = when {
            !isAxis -> true
            MAP_COORDINATE_NAMES.contains(getMappedDataLabel()) -> false
            else -> myIsContinuous
        }
    }

    protected open fun initLabel(): String {
        fun getAxisLabels() = listOf(Aes.X, Aes.Y).mapNotNull { axisAes ->
            if (myDataAccess.isMapped(axisAes)) {
                myDataAccess.getMappedDataLabel(axisAes)
            } else {
                null
            }
        }

        val dataLabel = getMappedDataLabel()
        return when {
            isAxis -> ""
            dataLabel.isEmpty() -> ""
            dataLabel in getAxisLabels() -> ""
            else -> dataLabel
        }
    }

    override fun getDataPoint(index: Int): DataPoint? {
        return if (!myIsTooltipAllowed) {
            null
        } else {
            DataPoint(
                label = myLabel,
                value = getMappedDataPointValue(index),
                isContinuous = myIsContinuous,
                aes = aes,
                isAxis = isAxis,
                isOutlier = isOutlier
            )
        }
    }

    protected fun getMappedDataLabel(): String {
        return myDataAccess.getMappedDataLabel(aes)
    }

    protected open fun getMappedDataPointValue(index: Int): String {
        return myDataAccess.getMappedData(aes, index).value
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
            MappedAes(aes, isOutlier = true, isAxis = true).also { it.setDataContext(dataContext) }

        fun createMappedAes(aes: Aes<*>, isOutlier: Boolean, dataContext: DataContext): ValueSource =
            MappedAes(aes, isOutlier = isOutlier, isAxis = false).also { it.setDataContext(dataContext) }
    }
}

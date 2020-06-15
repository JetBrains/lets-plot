/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.DataContext
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.interact.ValueSource

class ConstantAes(
    private val aes: Aes<*>,
    label: String,
    format: String
) : ValueSource {

    private lateinit var myDataAccess: MappedDataAccess
    private val myFormatter = if (format.isEmpty()) null else LineFormatter(format)
    private var myLabel: String = label
    private var myIsContinuous: Boolean = false

    override fun setDataContext(dataContext: DataContext) {
        myDataAccess = dataContext.mappedDataAccess
        require(myDataAccess.isMapped(aes)) { "$aes have to be mapped" }

        myIsContinuous = myDataAccess.isMappedDataContinuous(aes)
        myLabel = LineFormatter.chooseLabel(
            dataLabel = myDataAccess.getMappedDataLabel(aes),
            userLabel = myLabel
        )
    }

    override fun getDataPoint(index: Int): ValueSource.DataPoint? {
        val mappedValue = myDataAccess.getMappedData(aes, index).value
        return ValueSource.DataPoint(
            label = myLabel,
            value = myFormatter?.format(mappedValue, myIsContinuous) ?: mappedValue,
            isContinuous = myIsContinuous,
            aes = aes,
            isAxis = false,
            isOutlier = false
        )
    }
}
/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class StitchedPlotLayers(layers: List<jetbrains.datalore.plot.builder.GeomLayer>) {
    private val myLayers: List<jetbrains.datalore.plot.builder.GeomLayer>

    val legendKeyElementFactory: LegendKeyElementFactory
        get() {
            checkState(myLayers.isNotEmpty())
            return myLayers[0].legendKeyElementFactory
        }

    val aestheticsDefaults: AestheticsDefaults
        get() {
            checkState(myLayers.isNotEmpty())
            return myLayers[0].aestheticsDefaults
        }

    val isLegendDisabled: Boolean
        get() {
            checkState(myLayers.isNotEmpty())
            return myLayers[0].isLegendDisabled
        }

    init {
        myLayers = ArrayList(layers)
    }

    fun renderedAes(): List<Aes<*>> {
        return if (myLayers.isEmpty()) {
            emptyList()
        } else myLayers[0].renderedAes()
    }

    fun hasBinding(aes: Aes<*>): Boolean {
        return myLayers.isNotEmpty() && myLayers[0].hasBinding(aes)
    }

    fun hasConstant(aes: Aes<*>): Boolean {
        return myLayers.isNotEmpty() && myLayers[0].hasConstant(aes)
    }

    fun <T> getConstant(aes: Aes<T>): T {
        checkState(myLayers.isNotEmpty())
        return myLayers[0].getConstant(aes)
    }

    fun getBinding(aes: Aes<*>): VarBinding {
        checkState(myLayers.isNotEmpty())
        return myLayers[0].getBinding(aes)
    }

    fun getScale(aes: Aes<*>): Scale<*> {
        checkState(myLayers.isNotEmpty())
        return myLayers[0].scaleMap[aes]
    }

    fun getScaleMap(): TypedScaleMap {
        checkState(myLayers.isNotEmpty())
        return myLayers[0].scaleMap
    }

    fun getDataRange(`var`: DataFrame.Variable): ClosedRange<Double>? {
        checkState(isNumericData(`var`), "Not numeric data [$`var`]")
        var result: ClosedRange<Double>? = null
        for (layer in myLayers) {
            val range = layer.dataFrame.range(`var`)
            result = SeriesUtil.span(result, range)
        }
        return result
    }

    fun isNumericData(`var`: DataFrame.Variable): Boolean {
        checkState(myLayers.isNotEmpty())
        for (layer in myLayers) {
            if (!layer.dataFrame.isNumeric(`var`)) {
                return false
            }
        }
        return true
    }
}

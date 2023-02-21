/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.common.data.SeriesUtil

internal class StitchedPlotLayer constructor(
    private val geomLayers: List<GeomLayer>
) {
    val isYOrientation: Boolean
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].isYOrientation
        }

    val legendKeyElementFactory: LegendKeyElementFactory
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].legendKeyElementFactory
        }

    val aestheticsDefaults: AestheticsDefaults
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].aestheticsDefaults
        }

    val isLegendDisabled: Boolean
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].isLegendDisabled
        }

    val colorByAes: Aes<Color>
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].colorByAes
        }

    val fillByAes: Aes<Color>
        get() {
            check(geomLayers.isNotEmpty())
            return geomLayers[0].fillByAes
        }

    fun renderedAes(): List<Aes<*>> {
        return if (geomLayers.isEmpty()) {
            emptyList()
        } else geomLayers[0].renderedAes()
    }

    fun hasBinding(aes: Aes<*>): Boolean {
        return geomLayers.isNotEmpty() && geomLayers[0].hasBinding(aes)
    }

    fun hasConstant(aes: Aes<*>): Boolean {
        return geomLayers.isNotEmpty() && geomLayers[0].hasConstant(aes)
    }

    fun <T> getConstant(aes: Aes<T>): T {
        check(geomLayers.isNotEmpty())
        return geomLayers[0].getConstant(aes)
    }

//    fun getBinding(aes: Aes<*>): VarBinding {
//        check(geomLayers.isNotEmpty())
//        return geomLayers[0].getBinding(aes)
//    }

    fun getDataRange(variable: DataFrame.Variable): DoubleSpan? {
        check(isNumericData(variable)) { "Not numeric data [$variable]" }
        var result: DoubleSpan? = null
        for (layer in geomLayers) {
            val range = layer.dataFrame.range(variable)
            result = SeriesUtil.span(result, range)
        }
        return result
    }

    private fun isNumericData(variable: DataFrame.Variable): Boolean {
        check(geomLayers.isNotEmpty())
        for (layer in geomLayers) {
            if (!layer.dataFrame.isNumeric(variable)) {
                return false
            }
        }
        return true
    }

    internal fun getVariables(): Set<DataFrame.Variable> {
        check(geomLayers.isNotEmpty())
        return geomLayers[0].dataFrame.variables()
    }

    internal fun hasVariable(v: DataFrame.Variable): Boolean {
        check(geomLayers.isNotEmpty())
        return geomLayers[0].dataFrame.has(v)
    }
}

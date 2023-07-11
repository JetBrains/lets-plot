/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

class LegendBreak(val label: String) {
    private val myLayers = ArrayList<LegendBreakLayer>()

    val minimumKeySize: DoubleVector
        get() {
            var minSize = DoubleVector.ZERO
            for (layer in myLayers) {
                val layerMinKeySize = layer.keyElementFactory.minimumKeySize(layer.dataPoint)
                minSize = minSize.max(layerMinKeySize)
            }
            return minSize
        }

    val isEmpty: Boolean
        get() = myLayers.isEmpty()

    fun addLayer(dataPoint: DataPointAesthetics, keyElementFactory: LegendKeyElementFactory) {
        myLayers.add(
            LegendBreakLayer(
                dataPoint,
                keyElementFactory
            )
        )
    }

    fun createKeyElement(size: DoubleVector): SvgGElement {
        val g = SvgGElement()

        for (layer in myLayers) {
            val keyElement = layer.keyElementFactory.createKeyElement(layer.dataPoint, size)
            g.children().add(keyElement)
        }

        return g
    }

    private class LegendBreakLayer(
        val dataPoint: DataPointAesthetics,
        val keyElementFactory: LegendKeyElementFactory
    )

    companion object {
        fun simple(
            label: String,
            dataPoint: DataPointAesthetics,
            keyElementFactory: LegendKeyElementFactory
        ): LegendBreak {
            val br = LegendBreak(label)
            br.addLayer(dataPoint, keyElementFactory)
            return br
        }
    }
}
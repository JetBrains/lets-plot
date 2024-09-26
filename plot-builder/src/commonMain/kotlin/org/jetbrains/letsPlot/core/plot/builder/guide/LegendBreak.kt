/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.theme.LegendTheme
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

    fun createKeyElement(size: DoubleVector, theme: LegendTheme): SvgGElement {
        val g = SvgGElement()

        if (theme.showKeyRect()) {
            val rectElement = LegendKeyElementFactory.createBackgroundRectForKey(
                size,
                color = theme.keyRectColor(),
                fill = theme.keyRectFill(),
                strokeWidth = theme.keyRectStrokeWidth(),
                lineType = theme.keyLineType()
            )
            g.children().add(rectElement)
        }

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
        // for demo
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
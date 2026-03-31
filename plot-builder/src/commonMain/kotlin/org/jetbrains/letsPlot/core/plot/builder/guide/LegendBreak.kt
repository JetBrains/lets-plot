/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
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

    fun hasSameVisualProperties(other: LegendBreak): Boolean {
        if (label != other.label) return false
        if (myLayers.size != other.myLayers.size) return false

        for (i in myLayers.indices) {
            val thisPoint = myLayers[i].dataPoint
            val otherPoint = other.myLayers[i].dataPoint

            // Compare all visual aesthetics that appear in the legend key
            if (thisPoint.color() != otherPoint.color()) return false
            if (thisPoint.fill() != otherPoint.fill()) return false
            if (thisPoint.shape() != otherPoint.shape()) return false
            if (thisPoint.size() != otherPoint.size()) return false
            if (thisPoint.stroke() != otherPoint.stroke()) return false
            if (thisPoint.linewidth() != otherPoint.linewidth()) return false
            if (thisPoint.alpha() != otherPoint.alpha()) return false
            if (thisPoint.lineType() != otherPoint.lineType()) return false
        }

        return true
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
/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.guide

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import kotlin.math.floor

class LegendBreak(val label: String) {
    private val myLayers = ArrayList<LegendBreakLayer>()

    fun preferredKeySize(themeKeySize: DoubleVector): DoubleVector {
        var preferredSize = DoubleVector.ZERO
        for (layer in myLayers) {
            val keySizeMultiplier = if (layer.keyElementFactory.supportsKeySizeMultiplier) {
                layer.keySizeMultiplier
            } else {
                UNIT_KEY_SIZE_MULTIPLIER
            }
            val layerSize = sizeWithMultiplier(
                themeKeySize,
                pretty(layer.keyElementFactory.minimumKeySize(layer.dataPoint)),
                keySizeMultiplier
            )
            preferredSize = preferredSize.max(layerSize)
        }
        return preferredSize
    }

    val isEmpty: Boolean
        get() = myLayers.isEmpty()

    fun addLayer(
        dataPoint: DataPointAesthetics,
        keyElementFactory: LegendKeyElementFactory,
        keySizeMultiplier: DoubleVector = UNIT_KEY_SIZE_MULTIPLIER
    ) {
        myLayers.add(
            LegendBreakLayer(
                dataPoint,
                keyElementFactory,
                keySizeMultiplier
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
        val keyElementFactory: LegendKeyElementFactory,
        val keySizeMultiplier: DoubleVector
    )

    companion object {
        val UNIT_KEY_SIZE_MULTIPLIER = DoubleVector(1.0, 1.0)

        private fun pretty(v: DoubleVector): DoubleVector {
            val margin = 1.0
            return DoubleVector(
                floor(v.x / 2) * 2 + 1.0 + margin,
                floor(v.y / 2) * 2 + 1.0 + margin
            )
        }

        private fun sizeWithMultiplier(
            themeKeySize: DoubleVector,
            minimumKeySize: DoubleVector,
            keySizeMultiplier: DoubleVector
        ): DoubleVector {
            fun adjust(themeSize: Double, minimumSize: Double, multiplier: Double): Double {
                val scaledSize = (themeSize * multiplier).coerceAtLeast(0.0)
                return if (multiplier < 1.0) scaledSize else maxOf(scaledSize, minimumSize)
            }

            return DoubleVector(
                adjust(themeKeySize.x, minimumKeySize.x, keySizeMultiplier.x),
                adjust(themeKeySize.y, minimumKeySize.y, keySizeMultiplier.y)
            )
        }

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

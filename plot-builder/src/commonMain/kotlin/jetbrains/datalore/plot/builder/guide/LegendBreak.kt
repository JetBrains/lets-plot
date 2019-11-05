/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.vis.svg.SvgGElement

class LegendBreak(val label: String) {
    private val myLayers = ArrayList<jetbrains.datalore.plot.builder.guide.LegendBreak.MyLayer>()

    val minimumKeySize: DoubleVector
        get() {
            var minSize = DoubleVector.ZERO
            for (layer in myLayers) {
                val layerMinKeySize = layer.keyElementFactory.minimumKeySize(layer.aesthetics)
                minSize = minSize.max(layerMinKeySize)
            }
            return minSize
        }

    val isEmpty: Boolean
        get() = myLayers.isEmpty()

    fun addLayer(aesthetics: DataPointAesthetics, keyElementFactory: LegendKeyElementFactory) {
        myLayers.add(jetbrains.datalore.plot.builder.guide.LegendBreak.MyLayer(aesthetics, keyElementFactory))
    }

    fun createKeyElement(size: DoubleVector): SvgGElement {
        val g = SvgGElement()

        for (layer in myLayers) {
            val keyElement = layer.keyElementFactory.createKeyElement(layer.aesthetics, size)
            g.children().add(keyElement)
        }

        return g
    }

    private class MyLayer internal constructor(internal val aesthetics: DataPointAesthetics, internal val keyElementFactory: LegendKeyElementFactory)

    companion object {
        fun simple(label: String, aesthetics: DataPointAesthetics, keyElementFactory: LegendKeyElementFactory): jetbrains.datalore.plot.builder.guide.LegendBreak {
            val br = jetbrains.datalore.plot.builder.guide.LegendBreak(label)
            br.addLayer(aesthetics, keyElementFactory)
            return br
        }
    }
}
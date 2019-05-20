package jetbrains.datalore.visualization.plot.gog.plot.guide

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory

class LegendBreak(val label: String) {
    private val myLayers = ArrayList<MyLayer>()

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
        myLayers.add(MyLayer(aesthetics, keyElementFactory))
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
        fun simple(label: String, aesthetics: DataPointAesthetics, keyElementFactory: LegendKeyElementFactory): LegendBreak {
            val br = LegendBreak(label)
            br.addLayer(aesthetics, keyElementFactory)
            return br
        }
    }
}
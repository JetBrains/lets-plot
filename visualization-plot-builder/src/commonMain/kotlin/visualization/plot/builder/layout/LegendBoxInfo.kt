package jetbrains.datalore.visualization.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.guide.LegendBox

abstract class LegendBoxInfo protected constructor(internal val size: DoubleVector) {

    open val isEmpty: Boolean
        get() = false

    abstract fun createLegendBox(): LegendBox

    companion object {
        val EMPTY: LegendBoxInfo = object : LegendBoxInfo(DoubleVector.ZERO) {
            override val isEmpty: Boolean
                get() = true

            override fun createLegendBox(): LegendBox {
                throw IllegalStateException("Empty legend box info")
            }
        }
    }

}

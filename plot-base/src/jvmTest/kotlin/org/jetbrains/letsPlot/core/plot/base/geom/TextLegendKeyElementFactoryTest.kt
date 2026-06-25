/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.test.Test

class TextLegendKeyElementFactoryTest {
    @Test
    fun omittedHaloColorUsesLegendKeyFill() {
        val keyFill = Color(32, 40, 48)
        val factory = TextLegendKeyElementFactory(haloWidth = 1.5)

        val key = factory.createKeyElement(
            textDataPoint(fill = Color.TRANSPARENT),
            DoubleVector(20.0, 20.0),
            keyFill
        )

        val haloGroup = key.children()[1] as SvgGElement
        val haloText = haloGroup.children().single() as SvgTextElement
        val style = haloText.getAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE).get() as String
        assertThat(style).contains("stroke:${keyFill.toHexColor()};")
    }

    private fun textDataPoint(fill: Color): DataPointAesthetics {
        return object : DataPointAesthetics() {
            override fun index(): Int = 0

            override fun group(): Int? = null

            @Suppress("UNCHECKED_CAST")
            override fun <T> get(aes: Aes<T>): T? {
                return when (aes) {
                    Aes.COLOR -> Color.BLACK
                    Aes.FILL -> fill
                    Aes.ALPHA -> 1.0
                    Aes.SIZE -> 4.0
                    Aes.FAMILY -> ""
                    Aes.FONTFACE -> "plain"
                    Aes.LINEHEIGHT -> 1.0
                    Aes.ANGLE -> 0.0
                    else -> null
                } as T?
            }

            override val colorAes: Aes<Color> = Aes.COLOR

            override val fillAes: Aes<Color> = Aes.FILL
        }
    }
}

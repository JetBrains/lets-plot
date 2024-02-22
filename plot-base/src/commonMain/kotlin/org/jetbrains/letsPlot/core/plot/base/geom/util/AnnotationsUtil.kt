/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.colorspace.HSL
import org.jetbrains.letsPlot.commons.colorspace.hslFromRgb
import org.jetbrains.letsPlot.commons.colorspace.rgbFromHsl
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.geom.LabelGeom
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle

object AnnotationsUtil {

    fun textSizeGetter(textStyle: TextStyle, ctx: GeomContext): (String, DataPointAesthetics) -> DoubleVector = { text, p ->
        TextUtil.measure(
            text,
            toTextDataPointAesthetics(TextParams(textStyle), p),
            ctx
        )
    }

    fun chooseColor(background: Color) = when {
        Colors.luminance(background) < 0.5 -> Color.WHITE // if fill is dark
        else -> Color.BLACK
    }

    // WCAG recommend a minimum contrast ratio of 4.5:1 for normal text and 3:1 for large text
    private const val CONTRAST_RATIO = 4.5

    // Choose text color using the Lightness correction to make it readable on the given background
    fun chooseColor(textColor: Color, background: Color): Color {
        fun correctLightness(color: Color, newLightness: Double): Color {
            val hsl = hslFromRgb(color)
            return rgbFromHsl(
                HSL(hsl.h, hsl.s, newLightness)
            )
        }

        var contrastRatio = Colors.contrastRatio(background, textColor)
        val step = when {
            Colors.luminance(background) < 0.5 -> 0.05  //dark background
            else -> -0.05
        }
        var newLightness = hslFromRgb(textColor).l + step
        var newColor = textColor

        while (contrastRatio < CONTRAST_RATIO && newLightness in 0.0..1.0) {
            newColor = correctLightness(textColor, newLightness)
            contrastRatio = Colors.contrastRatio(background, newColor)
            newLightness += step
        }
       return newColor
    }

    data class TextParams(
        val style: TextStyle,
        val color: Color? = null,
        val hjust: String? = null,
        val vjust: String? = null,
        val angle: Double? = null,
        val fill: Color? = null,
        val alpha: Double? = null,
    )

    private fun toTextDataPointAesthetics(
        textParams: TextParams,
        p: DataPointAesthetics = AestheticsBuilder().build().dataPointAt(0),
    ): DataPointAesthetics {
        return object : DataPointAestheticsDelegate(p) {
            override operator fun <T> get(aes: Aes<T>): T? {
                val value: Any? = when (aes) {
                    Aes.SIZE -> textParams.style.size / 2
                    Aes.FAMILY -> textParams.style.family
                    Aes.FONTFACE -> textParams.style.face.toString()
                    Aes.COLOR -> textParams.color ?: textParams.style.color
                    Aes.HJUST -> textParams.hjust ?: "middle"
                    Aes.VJUST -> textParams.vjust ?: "center"
                    Aes.FILL -> textParams.fill ?: Color.TRANSPARENT
                    Aes.ANGLE -> textParams.angle ?: 0.0
                    Aes.ALPHA -> textParams.alpha ?: 1.0
                    else -> super.get(aes)
                }
                @Suppress("UNCHECKED_CAST")
                return value as T?
            }
        }
    }

    fun createTextElement(
        text: String,
        location: DoubleVector,
        textParams: TextParams,
        geomContext: GeomContext,
    ): SvgGElement {
        return TextGeom().buildTextComponent(
            toTextDataPointAesthetics(textParams = textParams),
            location,
            text,
            sizeUnitRatio = 1.0,
            geomContext,
            boundsCenter = null
        )
    }

    fun createLabelElement(
        text: String,
        location: DoubleVector,
        textParams: TextParams,
        geomContext: GeomContext,
        boundsCenter: DoubleVector?,
    ): SvgGElement {
        return LabelGeom()
            .apply { borderWidth = 0.0; paddingFactor = 0.0;  }
            .buildTextComponent(
                toTextDataPointAesthetics(textParams),
                location,
                text,
                sizeUnitRatio = 1.0,
                geomContext,
                boundsCenter = boundsCenter
            )
    }
}
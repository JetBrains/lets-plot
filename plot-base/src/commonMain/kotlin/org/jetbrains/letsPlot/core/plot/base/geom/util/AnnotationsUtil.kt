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

    private const val LIGHTER_LUMINANCE = 1.0
    private const val DARKER_LUMINANCE = 0.15
    // WCAG recommend a minimum contrast ratio of 4.5:1 for normal text and 3:1 for large text
    private const val CONTRAST_RATIO = 3.0

    fun chooseColor(textColor: Color, background: Color): Color {
        val contrastRatio = Colors.contrastRatio(background, textColor)
        if (contrastRatio >= CONTRAST_RATIO) {
            return textColor
        }
        val newLightness = when {
            Colors.luminance(background) < 0.5 -> LIGHTER_LUMINANCE
            else -> DARKER_LUMINANCE
        }
        val hsl = hslFromRgb(textColor)
        return rgbFromHsl(
            HSL(hsl.h, hsl.s, newLightness)
        )
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
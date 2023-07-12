/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.core.commons.typedKey.TypedKey
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape

class Aes<T> private constructor(val name: String, val isNumeric: Boolean = true) : TypedKey<T> {

    val isColor: Boolean
        get() = org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isColor(this)

    init {
        org.jetbrains.letsPlot.core.plot.base.Aes.Companion.values.add(this)
    }

    override fun toString(): String {
        return "aes '$name'"
    }

    companion object {
        private val values = ArrayList<org.jetbrains.letsPlot.core.plot.base.Aes<*>>()

        val X: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("x")
        val Y: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("y")
        val Z: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("z")

        val COLOR: org.jetbrains.letsPlot.core.plot.base.Aes<Color> =
            org.jetbrains.letsPlot.core.plot.base.Aes("color", false)
        val FILL: org.jetbrains.letsPlot.core.plot.base.Aes<Color> =
            org.jetbrains.letsPlot.core.plot.base.Aes("fill", false)
        val PAINT_A: org.jetbrains.letsPlot.core.plot.base.Aes<Color> =
            org.jetbrains.letsPlot.core.plot.base.Aes("paint_a", false)
        val PAINT_B: org.jetbrains.letsPlot.core.plot.base.Aes<Color> =
            org.jetbrains.letsPlot.core.plot.base.Aes("paint_b", false)
        val PAINT_C: org.jetbrains.letsPlot.core.plot.base.Aes<Color> =
            org.jetbrains.letsPlot.core.plot.base.Aes("paint_c", false)
        val ALPHA: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("alpha")
        val SHAPE: org.jetbrains.letsPlot.core.plot.base.Aes<PointShape> =
            org.jetbrains.letsPlot.core.plot.base.Aes("shape", false)
        val LINETYPE: org.jetbrains.letsPlot.core.plot.base.Aes<LineType> =
            org.jetbrains.letsPlot.core.plot.base.Aes("linetype", false)

        val SIZE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("size")
        val STROKE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("stroke")
        val LINEWIDTH: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("linewidth")
        val STACKSIZE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("stacksize")
        val WIDTH: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("width")
        val HEIGHT: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("height")
        val BINWIDTH: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("binwidth")
        val VIOLINWIDTH: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("violinwidth")
        val WEIGHT: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("weight")
        val INTERCEPT: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("intercept")
        val SLOPE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("slope")
        val XINTERCEPT: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("xintercept")
        val YINTERCEPT: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("yintercept")
        val LOWER: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("lower")
        val MIDDLE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("middle")
        val UPPER: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("upper")
        val SAMPLE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("sample")
        val QUANTILE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("quantile")

        val XMIN: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("xmin")
        val XMAX: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("xmax")
        val YMIN: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("ymin")
        val YMAX: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("ymax")
        val XEND: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("xend")
        val YEND: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("yend")

        val MAP_ID: org.jetbrains.letsPlot.core.plot.base.Aes<Any> =
            org.jetbrains.letsPlot.core.plot.base.Aes("map_id", false)
        val FRAME: org.jetbrains.letsPlot.core.plot.base.Aes<String> =
            org.jetbrains.letsPlot.core.plot.base.Aes("frame", false)

        val SPEED: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("speed")
        val FLOW: org.jetbrains.letsPlot.core.plot.base.Aes<Double> = org.jetbrains.letsPlot.core.plot.base.Aes("flow")

        val LABEL: org.jetbrains.letsPlot.core.plot.base.Aes<Any> =
            org.jetbrains.letsPlot.core.plot.base.Aes("label", false)
        val FAMILY: org.jetbrains.letsPlot.core.plot.base.Aes<String> =
            org.jetbrains.letsPlot.core.plot.base.Aes("family", false)
        val FONTFACE: org.jetbrains.letsPlot.core.plot.base.Aes<String> =
            org.jetbrains.letsPlot.core.plot.base.Aes("fontface", false)
        val LINEHEIGHT: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("lineheight")

        // text horizontal justification (numbers [0..1] or predefined strings = new Aes<>(); not positional)
        val HJUST = org.jetbrains.letsPlot.core.plot.base.Aes<Any>("hjust", false)

        // text vertical justification (numbers [0..1] or predefined strings, not positional)
        val VJUST = org.jetbrains.letsPlot.core.plot.base.Aes<Any>("vjust", false)

        val ANGLE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("angle")

        // pie geom - defines size of sector
        val SLICE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("slice")
        // pie geom - to explode sector from center point, detaching it from the main pie
        val EXPLODE: org.jetbrains.letsPlot.core.plot.base.Aes<Double> =
            org.jetbrains.letsPlot.core.plot.base.Aes("explode")

        fun numeric(unfiltered: Iterable<org.jetbrains.letsPlot.core.plot.base.Aes<*>>): List<org.jetbrains.letsPlot.core.plot.base.Aes<Double>> {
            // safe to cast all 'numeric' aesthetics are 'Double'
            @Suppress("UNCHECKED_CAST")
            return unfiltered.filter { aes -> aes.isNumeric } as List<org.jetbrains.letsPlot.core.plot.base.Aes<Double>>
        }

        fun isPositional(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalXY(aes) ||
                    // SLOPE must be positional or
                    // `geom_abline(slope=number)` will not work.
                    // it should draw the same line as:
                    // `geom_abline(slope=number, intersept=0)`
                    // See: PlotUtil.createLayerAesthetics()
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLOPE
        }

        fun isPositionalXY(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalX(aes) ||
                    org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalY(aes)
        }

        fun isPositionalX(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XINTERCEPT ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMIN ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XMAX ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.XEND
        }

        fun isPositionalY(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMIN ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YMAX ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.INTERCEPT ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YINTERCEPT ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LOWER ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MIDDLE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.UPPER ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SAMPLE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.YEND
        }

        fun toAxisAes(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>, isYOrientation: Boolean): org.jetbrains.letsPlot.core.plot.base.Aes<*> {
            // Aes like `LOWER` (boxplot) are mapped on either X or Y-axis depending on the geom orientation.
            return when {
                aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X || aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y -> aes
                org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalX(aes) -> if (isYOrientation) org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y else org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X
                org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalY(aes) -> if (isYOrientation) org.jetbrains.letsPlot.core.plot.base.Aes.Companion.X else org.jetbrains.letsPlot.core.plot.base.Aes.Companion.Y
                else -> throw IllegalArgumentException("Expected a positional aes by was $aes")
            }
        }

        fun isColor(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.COLOR ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FILL ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_A ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_B ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.PAINT_C
        }

        fun affectingScaleX(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalX(aes)
        }

        fun affectingScaleY(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositionalY(aes) &&
                    // "INTERCEPT" is "positional Y" because it must use the same 'mapper' as other "positional Y"-s,
                    // but its range of values is not taken in account when computing the Y-mapper.
                    aes != org.jetbrains.letsPlot.core.plot.base.Aes.Companion.INTERCEPT
        }

        fun affectingScaleX(unfiltered: Iterable<org.jetbrains.letsPlot.core.plot.base.Aes<*>>): List<org.jetbrains.letsPlot.core.plot.base.Aes<Double>> {
            val numeric = org.jetbrains.letsPlot.core.plot.base.Aes.Companion.numeric(unfiltered)
            return numeric.filter { org.jetbrains.letsPlot.core.plot.base.Aes.Companion.affectingScaleX(it) }
        }

        fun affectingScaleY(unfiltered: Iterable<org.jetbrains.letsPlot.core.plot.base.Aes<*>>): List<org.jetbrains.letsPlot.core.plot.base.Aes<Double>> {
            val numeric = org.jetbrains.letsPlot.core.plot.base.Aes.Companion.numeric(unfiltered)
            return numeric.filter { org.jetbrains.letsPlot.core.plot.base.Aes.Companion.affectingScaleY(it) }
        }

        fun noGuideNeeded(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
            return aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.MAP_ID ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FRAME ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SPEED ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FLOW ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LABEL ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLOPE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.STACKSIZE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.WIDTH ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HEIGHT ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.BINWIDTH ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VIOLINWIDTH ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.QUANTILE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.HJUST ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.VJUST ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.ANGLE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FAMILY ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.FONTFACE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.LINEHEIGHT ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.SLICE ||
                    aes == org.jetbrains.letsPlot.core.plot.base.Aes.Companion.EXPLODE ||
                    org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositional(aes)
        }

        fun values(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
            return org.jetbrains.letsPlot.core.plot.base.Aes.Companion.values
        }

        fun allPositional(): List<org.jetbrains.letsPlot.core.plot.base.Aes<Double>> {
            @Suppress("UNCHECKED_CAST")
            return org.jetbrains.letsPlot.core.plot.base.Aes.Companion.values.filter {
                org.jetbrains.letsPlot.core.plot.base.Aes.Companion.isPositional(
                    it
                )
            } as List<org.jetbrains.letsPlot.core.plot.base.Aes<Double>>
        }
    }
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.typedKey.TypedKey
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShape

class Aes<T> private constructor(val name: String, val isNumeric: Boolean = true) : TypedKey<T> {

    val isColor: Boolean
        get() = isColor(this)

    init {
        values.add(this)
    }

    override fun toString(): String {
        return "aes '$name'"
    }

    companion object {
        private val values = ArrayList<Aes<*>>()

        val X: Aes<Double> = Aes("x")
        val Y: Aes<Double> = Aes("y")
        val Z: Aes<Double> = Aes("z")

        val COLOR: Aes<Color> =
            Aes("color", false)
        val FILL: Aes<Color> =
            Aes("fill", false)
        val PAINT_A: Aes<Color> =
            Aes("paint_a", false)
        val PAINT_B: Aes<Color> =
            Aes("paint_b", false)
        val PAINT_C: Aes<Color> =
            Aes("paint_c", false)
        val ALPHA: Aes<Double> =
            Aes("alpha")
        val SHAPE: Aes<PointShape> =
            Aes("shape", false)
        val LINETYPE: Aes<LineType> =
            Aes("linetype", false)

        val SIZE: Aes<Double> = Aes("size")
        val STROKE: Aes<Double> =
            Aes("stroke")
        val LINEWIDTH: Aes<Double> =
            Aes("linewidth")
        val STACKSIZE: Aes<Double> =
            Aes("stacksize")
        val WIDTH: Aes<Double> =
            Aes("width")
        val HEIGHT: Aes<Double> =
            Aes("height")
        val BINWIDTH: Aes<Double> =
            Aes("binwidth")
        val VIOLINWIDTH: Aes<Double> =
            Aes("violinwidth")
        val WEIGHT: Aes<Double> =
            Aes("weight")
        val INTERCEPT: Aes<Double> =
            Aes("intercept")
        val SLOPE: Aes<Double> =
            Aes("slope")
        val XINTERCEPT: Aes<Double> =
            Aes("xintercept")
        val YINTERCEPT: Aes<Double> =
            Aes("yintercept")
        val LOWER: Aes<Double> =
            Aes("lower")
        val MIDDLE: Aes<Double> =
            Aes("middle")
        val UPPER: Aes<Double> =
            Aes("upper")
        val XLOWER: Aes<Double> =
            Aes("xlower")
        val XMIDDLE: Aes<Double> =
            Aes("xmiddle")
        val XUPPER: Aes<Double> =
            Aes("xupper")
        val SAMPLE: Aes<Double> =
            Aes("sample")
        val QUANTILE: Aes<Double> =
            Aes("quantile")

        val XMIN: Aes<Double> = Aes("xmin")
        val XMAX: Aes<Double> = Aes("xmax")
        val YMIN: Aes<Double> = Aes("ymin")
        val YMAX: Aes<Double> = Aes("ymax")
        val XEND: Aes<Double> = Aes("xend")
        val YEND: Aes<Double> = Aes("yend")

        val MAP_ID: Aes<Any> =
            Aes("map_id", false)
        val FRAME: Aes<String> =
            Aes("frame", false)

        val SPEED: Aes<Double> =
            Aes("speed")
        val FLOW: Aes<Double> = Aes("flow")

        val LABEL: Aes<Any> =
            Aes("label", false)
        val FAMILY: Aes<String> =
            Aes("family", false)
        val FONTFACE: Aes<String> =
            Aes("fontface", false)
        val LINEHEIGHT: Aes<Double> =
            Aes("lineheight")

        // text horizontal justification (numbers [0..1] or predefined strings = new Aes<>(); not positional)
        val HJUST = Aes<Any>("hjust", false)

        // text vertical justification (numbers [0..1] or predefined strings, not positional)
        val VJUST = Aes<Any>("vjust", false)

        val ANGLE: Aes<Double> =
            Aes("angle")
        val RADIUS: Aes<Double> =
            Aes("radius")

        // pie geom - defines size of sector
        val SLICE: Aes<Double> =
            Aes("slice")
        // pie geom - to explode sector from center point, detaching it from the main pie
        val EXPLODE: Aes<Double> =
            Aes("explode")

        val SIZE_START: Aes<Double> = Aes("size_start")
        val SIZE_END: Aes<Double> = Aes("size_end")
        val STROKE_START: Aes<Double> = Aes("stroke_start")
        val STROKE_END: Aes<Double> = Aes("stroke_end")

        val POINT_SIZE: Aes<Double> = Aes("point_size")
        val SEGMENT_COLOR: Aes<Color> = Aes("segment_color", false)
        val SEGMENT_SIZE: Aes<Double> = Aes("segment_size")
        val SEGMENT_ALPHA: Aes<Double> = Aes("segment_alpha")

        fun numeric(unfiltered: Iterable<Aes<*>>): List<Aes<Double>> {
            // safe to cast all 'numeric' aesthetics are 'Double'
            @Suppress("UNCHECKED_CAST")
            return unfiltered.filter { aes -> aes.isNumeric } as List<Aes<Double>>
        }

        fun isPositional(aes: Aes<*>): Boolean {
            return isPositionalXY(aes) ||
                    // SLOPE must be positional or
                    // `geom_abline(slope=number)` will not work.
                    // it should draw the same line as:
                    // `geom_abline(slope=number, intersept=0)`
                    // See: PlotUtil.createLayerAesthetics()
                    aes == SLOPE
        }

        fun isPositionalXY(aes: Aes<*>): Boolean {
            return isPositionalX(aes) ||
                    isPositionalY(aes)
        }

        fun isPositionalX(aes: Aes<*>): Boolean {
            return aes == X ||
                    aes == XINTERCEPT ||
                    aes == XLOWER ||
                    aes == XMIDDLE ||
                    aes == XUPPER ||
                    aes == XMIN ||
                    aes == XMAX ||
                    aes == XEND
        }

        fun isPositionalY(aes: Aes<*>): Boolean {
            return aes == Y ||
                    aes == YMIN ||
                    aes == YMAX ||
                    aes == INTERCEPT ||
                    aes == YINTERCEPT ||
                    aes == LOWER ||
                    aes == MIDDLE ||
                    aes == UPPER ||
                    aes == SAMPLE ||
                    aes == YEND
        }

        fun toAxisAes(aes: Aes<*>): Aes<*> {
            return when {
                isPositionalX(aes) -> X
                isPositionalY(aes) -> Y
                else -> throw IllegalArgumentException("Expected a positional aes by was $aes")
            }
        }

        fun isColor(aes: Aes<*>): Boolean {
            return aes == COLOR ||
                    aes == FILL ||
                    aes == PAINT_A ||
                    aes == PAINT_B ||
                    aes == PAINT_C ||
                    aes == SEGMENT_COLOR
        }

        fun affectingScaleX(aes: Aes<*>): Boolean {
            return isPositionalX(aes)
        }

        fun affectingScaleY(aes: Aes<*>): Boolean {
            return isPositionalY(aes) &&
                    // "INTERCEPT" is "positional Y" because it must use the same 'mapper' as other "positional Y"-s,
                    // but its range of values is not taken in account when computing the Y-mapper.
                    aes != INTERCEPT
        }

        fun affectingScaleX(unfiltered: Iterable<Aes<*>>): List<Aes<Double>> {
            val numeric = numeric(unfiltered)
            return numeric.filter { affectingScaleX(it) }
        }

        fun affectingScaleY(unfiltered: Iterable<Aes<*>>): List<Aes<Double>> {
            val numeric = numeric(unfiltered)
            return numeric.filter { affectingScaleY(it) }
        }

        fun noGuideNeeded(aes: Aes<*>): Boolean {
            return aes == MAP_ID ||
                    aes == FRAME ||
                    aes == SPEED ||
                    aes == FLOW ||
                    aes == LABEL ||
                    aes == SLOPE ||
                    aes == STACKSIZE ||
                    aes == WIDTH ||
                    aes == HEIGHT ||
                    aes == BINWIDTH ||
                    aes == VIOLINWIDTH ||
                    aes == QUANTILE ||
                    aes == HJUST ||
                    aes == VJUST ||
                    aes == ANGLE ||
                    aes == RADIUS ||
                    aes == FAMILY ||
                    aes == FONTFACE ||
                    aes == LINEHEIGHT ||
                    aes == SLICE ||
                    aes == EXPLODE ||
                    aes == SIZE_START ||
                    aes == SIZE_END ||
                    aes == STROKE_START ||
                    aes == STROKE_END ||
                    aes == POINT_SIZE ||
                    aes == SEGMENT_SIZE ||
                    isPositional(aes)
        }

        fun values(): List<Aes<*>> {
            return values
        }

        fun allPositional(): List<Aes<Double>> {
            @Suppress("UNCHECKED_CAST")
            return values.filter {
                isPositional(
                    it
                )
            } as List<Aes<Double>>
        }
    }
}

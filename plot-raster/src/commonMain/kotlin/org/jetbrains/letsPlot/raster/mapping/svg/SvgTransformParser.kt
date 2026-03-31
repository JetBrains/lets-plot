/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
import kotlin.math.PI
import kotlin.math.sin

internal object SvgTransformParser {
    private const val SCALE_X = 0
    private const val SCALE_Y = 1
    private const val SKEW_X_ANGLE = 0
    private const val SKEW_Y_ANGLE = 0
    private const val ROTATE_ANGLE = 0
    private const val ROTATE_X = 1
    private const val ROTATE_Y = 2
    private const val TRANSLATE_X = 0
    private const val TRANSLATE_Y = 1

    private fun toRadians(degrees: Double): Double = (degrees * PI / 180)

    fun parseSvgTransform(svgTransform: String): List<AffineTransform> {
        val transformData = parseTransform(svgTransform)

        val transforms = ArrayList<AffineTransform>()
        for (res in transformData) {
            val transform: AffineTransform =
                when (res.name) {
                    SvgTransform.SCALE -> {
                        val scaleX = res.getParam(SCALE_X)!!
                        val scaleY = res.getParam(SCALE_Y) ?: scaleX
                        AffineTransform.makeScale(scaleX, scaleY)
                    }

                    SvgTransform.SKEW_X -> {
                        val angle = res.getParam(SKEW_X_ANGLE)!!
                        val factor = sin(toRadians(angle))
                        AffineTransform.makeShear(factor, 0.0)
                    }

                    SvgTransform.SKEW_Y -> {
                        val angle = res.getParam(SKEW_Y_ANGLE)!!
                        val factor = sin(toRadians(angle))
                        AffineTransform.makeShear(0.0, factor)
                    }

                    SvgTransform.ROTATE -> {
                        val angle = toRadians(res.getParam(ROTATE_ANGLE)!!)
                        val pivotX = if (res.paramCount == 3) res.getParam(ROTATE_X)!! else 0.0
                        val pivotY = if (res.paramCount == 3) res.getParam(ROTATE_Y)!! else 0.0
                        AffineTransform.makeRotation(angle, pivotX, pivotY)
                    }

                    SvgTransform.TRANSLATE -> {
                        val dX = res.getParam(TRANSLATE_X)!!
                        val dY = res.getParam(TRANSLATE_Y) ?: 0.0
                        AffineTransform.makeTranslation(dX, dY)
                    }

                    SvgTransform.MATRIX -> error("UNSUPPORTED: We don't use MATRIX")

                    else -> throw IllegalArgumentException("Unknown transform: " + res.name)
                }
            transforms.add(transform)
        }

        return transforms
    }

    private const val OPTIONAL_PARAM = "(-?\\d+\\.?\\d*)?,? ?"

    private val TRANSFORM = MyPatternBuilder("(").or(
        SvgTransform.TRANSLATE,
        SvgTransform.ROTATE,
        SvgTransform.SCALE,
        SvgTransform.SKEW_X,
        SvgTransform.SKEW_Y,
        SvgTransform.MATRIX
    )
        .append(")\\( ?(-?\\d+\\.?\\d*),? ?").pluralAppend(OPTIONAL_PARAM, 5).append("\\)").toString()

    private val TRANSFORM_EXP = Regex(TRANSFORM) //RegExp.compile(TRANSFORM, "g")

    private const val NAME_INDEX = 1
    private const val FIRST_PARAM_INDEX = 2


    private fun parseTransform(input: String?): List<TransformData> {
        return parse(
            input,
            TRANSFORM_EXP
        )
    }

    private fun getName(matcher: MatchResult): String {
        return matcher.groupValues[NAME_INDEX]
    }

    private fun getParam(matcher: MatchResult, i: Int): String? {
        return matcher.groupValues[i + FIRST_PARAM_INDEX]
    }

    private fun getParamCount(matcher: MatchResult): Int {
        return matcher.groupValues.size - FIRST_PARAM_INDEX
    }

    private fun parse(input: String?, regExp: Regex): List<TransformData> {
        if (input == null) return listOf()

        val transformData = ArrayList<TransformData>()
        var matcher: MatchResult? = regExp.find(input)
        while (matcher != null) {
            val paramCount = getParamCount(matcher)
            val r = TransformData(
                getName(matcher), paramCount
            )
            for (i in 0 until paramCount) {
                val g = getParam(matcher, i) ?: break
                if (g == "") break
                r.addParam(g)
            }
            transformData.add(r)
            matcher = matcher.next()
        }
        return transformData
    }

    private class MyPatternBuilder(s: String) {
        private val sb: StringBuilder = StringBuilder(s)

        fun append(s: String): MyPatternBuilder {
            sb.append(s)
            return this
        }

        fun pluralAppend(s: String, count: Int): MyPatternBuilder {
            for (i in 0 until count) {
                sb.append(s)
            }
            return this
        }

        fun or(vararg ss: String): MyPatternBuilder {
            val ssLastIndex = ss.size - 1
            for ((index, s) in ss.withIndex()) {
                sb.append(s)
                if (index < ssLastIndex) {
                    sb.append('|')
                }
            }
            return this
        }

        override fun toString(): String {
            return sb.toString()
        }
    }

    internal class TransformData(val name: String, paramCount: Int) {
        private val myParams: MutableList<Double?> = ArrayList(paramCount)

        val args: List<Double?>
            get() = myParams

        val paramCount: Int
            get() = myParams.size

        fun addParam(p: String?) {
            myParams.add(if (p == "") null else p?.toDouble())
        }

        fun getParam(i: Int): Double? {
            if (!containsParam(i)) {
                return null
            }
            return myParams[i]
        }

        fun containsParam(i: Int): Boolean {
            return i < paramCount
        }
    }
}

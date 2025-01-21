/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
import org.jetbrains.letsPlot.raster.shape.Matrix33
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
    private const val MATRIX_11 = 0
    private const val MATRIX_12 = 1
    private const val MATRIX_21 = 2
    private const val MATRIX_22 = 3
    private const val MATRIX_DX = 4
    private const val MATRIX_DY = 5

    private fun toRadians(degrees: Float): Float = (degrees * PI / 180).toFloat()

    fun parseSvgTransform(svgTransform: String): List<Matrix33> {
        val results = parseTransform(svgTransform)

        val transforms = ArrayList<Matrix33>()
        for (res in results) {
            val transform: Matrix33 =
                when (res.name) {
                    SvgTransform.SCALE -> {
                        val scaleX = res.getParam(SCALE_X)!!
                        val scaleY = res.getParam(SCALE_Y) ?: scaleX
                        Matrix33.makeScale(scaleX, scaleY)
                    }

                    SvgTransform.SKEW_X -> {
                        val angle = res.getParam(SKEW_X_ANGLE)!!
                        val factor = sin(toRadians(angle))
                        Matrix33.makeSkew(factor, 0.0f)
                    }

                    SvgTransform.SKEW_Y -> {
                        val angle = res.getParam(SKEW_Y_ANGLE)!!
                        val factor = sin(toRadians(angle))
                        Matrix33.makeSkew(0.0f, factor)
                    }

                    SvgTransform.ROTATE -> {
                        val angle = res.getParam(ROTATE_ANGLE)!!
                        val pivotX = if (res.paramCount == 3) res.getParam(ROTATE_X)!! else 0.0f
                        val pivotY = if (res.paramCount == 3) res.getParam(ROTATE_Y)!! else 0.0f
                        Matrix33.makeRotate(angle, pivotX, pivotY)
                    }

                    SvgTransform.TRANSLATE -> {
                        val dX = res.getParam(TRANSLATE_X)!!
                        val dY = res.getParam(TRANSLATE_Y) ?: 0.0f
                        Matrix33.makeTranslate(dX, dY)
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

    private val PATH = MyPatternBuilder("(").charset(SvgPathData.Action.values()).append(") ?")
        .pluralAppend(OPTIONAL_PARAM, 7).toString()

    private val TRANSFORM_EXP = Regex(TRANSFORM) //RegExp.compile(TRANSFORM, "g")
    private val PATH_EXP = Regex(PATH) //RegExp.compile(PATH, "g")

    private const val NAME_INDEX = 1
    private const val FIRST_PARAM_INDEX = 2


    private fun parseTransform(input: String?): List<Result> {
        return parse(
            input,
            TRANSFORM_EXP
        )
    }

    fun parsePath(input: String?): List<Result> {
        return parse(
            input,
            PATH_EXP
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

    private fun parse(input: String?, regExp: Regex): List<Result> {
        if (input == null) return listOf()

        val results = ArrayList<Result>()
        var matcher: MatchResult? = regExp.find(input)
        while (matcher != null) {
            val paramCount = getParamCount(matcher)
            val r = Result(
                getName(matcher), paramCount
            )
            for (i in 0 until paramCount) {
                val g = getParam(matcher, i) ?: break
                if (g == "") break
                r.addParam(g)
            }
            results.add(r)
            matcher = matcher.next()
        }
        return results
    }

    private class MyPatternBuilder internal constructor(s: String) {
        private val sb: StringBuilder = StringBuilder(s)

        internal fun append(s: String): MyPatternBuilder {
            sb.append(s)
            return this
        }

        internal fun pluralAppend(s: String, count: Int): MyPatternBuilder {
            for (i in 0 until count) {
                sb.append(s)
            }
            return this
        }

        internal fun or(vararg ss: String): MyPatternBuilder {
            val ssLastIndex = ss.size - 1
            for ((index, s) in ss.withIndex()) {
                sb.append(s)
                if (index < ssLastIndex) {
                    sb.append('|')
                }
            }
            return this
        }

        internal fun charset(actions: Array<SvgPathData.Action>): MyPatternBuilder {
            sb.append('[')
            for (v in actions) {
                sb.append(v.absoluteCmd())
                sb.append(v.relativeCmd())
            }
            sb.append(']')
            return this
        }

        override fun toString(): String {
            return sb.toString()
        }
    }

    internal class Result constructor(val name: String, paramCount: Int) {
        private val myParams: MutableList<Float?> = ArrayList(paramCount)

        val params: List<Float?>
            get() = myParams

        val paramCount: Int
            get() = myParams.size

        fun addParam(p: String?) {
            myParams.add(if (p == "") null else p?.toFloat())
        }

        fun getParam(i: Int): Float? {
            if (!containsParam(i)) {
                throw IndexOutOfBoundsException("index: $i; size: $paramCount; name: $name")
            }
            return myParams[i]
        }

        fun getVector(startIndex: Int): DoubleVector {
            return DoubleVector(getParam(startIndex)!!, getParam(startIndex + 1)!!)
        }

        fun containsParam(i: Int): Boolean {
            return i < paramCount
        }
    }
}

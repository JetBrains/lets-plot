/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg.util

import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData.Action

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform

internal object ParsingUtil {
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

    private val PATH = MyPatternBuilder("(").charset(Action.values()).append(") ?")
            .pluralAppend(OPTIONAL_PARAM, 7).toString()

    private val TRANSFORM_EXP = Regex(TRANSFORM) //RegExp.compile(TRANSFORM, "g")
    private val PATH_EXP = Regex(PATH) //RegExp.compile(PATH, "g")

    private const val NAME_INDEX = 1
    private const val FIRST_PARAM_INDEX = 2


    fun parseTransform(input: String?): List<Result> {
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

        internal fun charset(actions: Array<Action>): MyPatternBuilder {
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
        private val myParams: MutableList<Double?> = ArrayList(paramCount)

        val params: List<Double?>
            get() = myParams

        val paramCount: Int
            get() = myParams.size

        fun addParam(p: String?) {
            myParams.add(if (p == "") null else p?.toDouble())
        }

        fun getParam(i: Int): Double? {
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

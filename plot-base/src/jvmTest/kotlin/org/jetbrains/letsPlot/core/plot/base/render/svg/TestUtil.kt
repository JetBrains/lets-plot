/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Colors.parseColor
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import kotlin.math.pow

object TestUtil {

    fun SvgTSpanElement.wholeText(): String {
        return children()
            .map { it as SvgTextNode }
            .joinToString(separator = "") { it.textContent().get() }
    }

    fun SvgTextElement.tspans(): List<SvgTSpanElement> = children().map { it as SvgTSpanElement }

    fun SvgTextElement.stringParts(): List<String> = children().flatMap { item ->
        when (item) {
            is SvgTextNode -> listOf(item.textContent().get())
            is SvgTSpanElement -> item.children().map { (it as SvgTextNode).textContent().get() }
            is SvgAElement -> item.children().map { aChild ->
                (aChild as SvgTSpanElement).children().single().let { tSpanChild ->
                    (tSpanChild as SvgTextNode).textContent().get()
                }
            }
            else -> error("Unexpected element type")
        }
    }

    fun Iterable<SvgTextElement>.lineParts(): List<List<String>> = map { it.stringParts() }
    fun Iterable<SvgTextElement>.tspans(): List<List<SvgTSpanElement>> = map { it.tspans() }


    fun assertTSpan(
        tspan: SvgTSpanElement,
        text: String,
        bold: Boolean = false,
        italic: Boolean = false,
        color: String? = null
    ) {
        assertThat(tspan.wholeText()).isEqualTo(text)

        if (bold) {
            assertThat(tspan.fontWeight().get()).isEqualTo("bold")
        } else {
            assertThat(tspan.fontWeight().get()).isNull()
        }

        if (italic) {
            assertThat(tspan.fontStyle().get()).isEqualTo("italic")
        } else {
            assertThat(tspan.fontStyle().get()).isNull()
        }

        if (color != null) {
            assertThat(tspan.fill().get()).isEqualTo(SvgColors.create(parseColor(color)))
        } else {
            assertThat(tspan.fill().get()).isNull()
        }
    }

    fun assertFormulaTSpan(
        tspan: SvgTSpanElement,
        text: String,
        level: FormulaLevel,
        bold: Boolean = false,
        italic: Boolean = false,
        color: String? = null
    ) {
        assertTSpan(tspan, text, bold, italic, color)

        val expectedDy = level.dy()
        if (expectedDy != null) {
            assertThat(tspan.textDy().get()).isEqualTo(expectedDy)
        }

        val expectedSize = level.size()
        if (expectedSize != null) {
            assertThat(tspan.getAttribute(SvgTextContent.FONT_SIZE).get()).isEqualTo(expectedSize)
        }
    }

    class FormulaLevel {
        private val shifts: MutableList<Shift> = mutableListOf()

        fun pass(): FormulaLevel {
            shifts.add(Shift.PASS)
            return this
        }

        fun sup(): FormulaLevel {
            shifts.add(Shift.SUPERSCRIPT)
            return this
        }

        fun sub(): FormulaLevel {
            shifts.add(Shift.SUBSCRIPT)
            return this
        }

        fun revert(): FormulaLevel {
            shifts.add(Shift.REVERT)
            return this
        }

        fun size(): String? {
            val shiftsStack = ArrayDeque<Shift>()
            var level = 0
            var size: Double? = null
            shifts.forEach { shift ->
                when (shift) {
                    Shift.PASS -> {
                        size = null
                    }
                    Shift.SUPERSCRIPT,
                    Shift.SUBSCRIPT -> {
                        shiftsStack.addLast(Shift.SUBSCRIPT)
                        level += 1
                        size = 0.7.pow(level)
                    }
                    Shift.REVERT -> {
                        size = 0.7.pow(level)
                        level += when (shiftsStack.removeLast()) {
                            Shift.SUBSCRIPT,
                            Shift.SUPERSCRIPT -> -1
                            else -> error("Unexpected shift type")
                        }
                    }
                }
            }
            return size?.let { "${it}em" }
        }

        fun dy(): String? {
            val shiftsStack = ArrayDeque<Shift>()
            var dy: Double? = null
            shifts.forEach { shift ->
                when (shift) {
                    Shift.PASS -> {
                        dy = null
                    }
                    Shift.SUPERSCRIPT -> {
                        shiftsStack.addLast(Shift.SUPERSCRIPT)
                        dy = -0.4
                    }
                    Shift.SUBSCRIPT -> {
                        shiftsStack.addLast(Shift.SUBSCRIPT)
                        dy = 0.4
                    }
                    Shift.REVERT -> {
                        dy = when (shiftsStack.removeLast()) {
                            Shift.SUBSCRIPT -> -0.4
                            Shift.SUPERSCRIPT -> 0.4
                            else -> error("Unexpected shift type")
                        }
                    }
                }
            }
            return dy?.let { "${it}em" }
        }

        enum class Shift {
            PASS, SUPERSCRIPT, SUBSCRIPT, REVERT;
        }
    }
}
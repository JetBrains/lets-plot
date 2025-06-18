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


    fun assertTSpan(
        tspan: SvgTSpanElement,
        text: String,
        x: Double? = null,
        bold: Boolean = false,
        italic: Boolean = false,
        color: String? = null
    ) {
        assertThat(tspan.wholeText()).isEqualTo(text)

        if (x != null) {
            assertThat(tspan.x().get()).isEqualTo(x)
        } else {
            assertThat(tspan.x().get()).isNull()
        }

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
        text: String?,
        level: FormulaLevel,
        expectedX: Double? = null,
        expectedAnchor: String? = null,
        bold: Boolean = false,
        italic: Boolean = false,
        color: String? = null
    ) {
        assertTSpan(tspan, text ?: tspan.wholeText(), expectedX, bold, italic, color)

        val expectedDy = level.dy()
        when {
            expectedDy != null -> assertThat(tspan.textDy().get()).isEqualTo(expectedDy)
            !level.toPass() -> assertThat(tspan.textDy().get()).isNull()
        }

        val expectedSize = level.size()
        when {
            expectedSize != null -> assertThat(tspan.getAttribute(SvgTextContent.FONT_SIZE).get()).isEqualTo(expectedSize)
            !level.toPass() -> assertThat(tspan.getAttribute(SvgTextContent.FONT_SIZE).get()).isNull()
        }

        val anchor: String? = tspan.textAnchor().get()
        if (expectedAnchor != null) {
            assertThat(anchor).isEqualTo(expectedAnchor)
        } else {
            assertThat(anchor).isNull()
        }
    }

    // Represents a level of formula rendering (superscript, subscript, fraction, etc.)
    // Helps to calculate the size and dy (vertical shift) for the text elements in test cases
    class FormulaLevel {
        private val shifts: MutableList<Shift> = mutableListOf()

        fun copy(): FormulaLevel {
            val level = FormulaLevel()
            shifts.forEach { shift -> level.shifts.add(shift) }
            return level
        }

        // The level remains unchanged
        fun current(): FormulaLevel {
            shifts.add(Shift.CURRENT)
            return this
        }

        // No checks
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

        fun num(): FormulaLevel {
            shifts.add(Shift.NUMERATOR)
            return this
        }

        fun denom(): FormulaLevel {
            shifts.add(Shift.DENOMINATOR)
            return this
        }

        fun bar(): FormulaLevel {
            shifts.add(Shift.FRACTION_BAR)
            return this
        }

        fun revert(): FormulaLevel {
            shifts.add(Shift.REVERT)
            return this
        }

        fun toPass(): Boolean {
            return shifts.isEmpty() || shifts.last() == Shift.PASS
        }

        fun sizeValue(): Double? {
            val shiftsStack = ArrayDeque<Shift>()
            val sizeByLevel = { level: Int ->
                if (level > 0) {
                    0.7.pow(level)
                } else {
                    null
                }
            }
            var level = 0
            var size: Double? = null
            shifts.forEach { shift ->
                when (shift) {
                    Shift.PASS -> {
                        size = null
                    }
                    Shift.CURRENT,
                    Shift.NUMERATOR,
                    Shift.DENOMINATOR -> {
                        size = sizeByLevel(level)
                    }
                    Shift.FRACTION_BAR -> {
                        shiftsStack.addLast(Shift.FRACTION_BAR)
                        size = sizeByLevel(level)
                    }
                    Shift.SUPERSCRIPT -> {
                        shiftsStack.addLast(Shift.SUPERSCRIPT)
                        level += 1
                        size = sizeByLevel(level)
                    }
                    Shift.SUBSCRIPT -> {
                        shiftsStack.addLast(Shift.SUBSCRIPT)
                        level += 1
                        size = sizeByLevel(level)
                    }
                    Shift.REVERT -> {
                        size = sizeByLevel(level)
                        level += when (shiftsStack.removeLast()) {
                            Shift.SUBSCRIPT,
                            Shift.SUPERSCRIPT -> -1
                            Shift.FRACTION_BAR -> 0
                            else -> error("Unexpected shift type")
                        }
                    }
                }
            }
            return size
        }

        fun size(): String? {
            return sizeValue()?.let { "${it}em" }
        }

        fun dy(): String? {
            val shiftsStack = ArrayDeque<Shift>()
            var dy: Double? = null
            shifts.forEach { shift ->
                when (shift) {
                    Shift.CURRENT,
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
                    Shift.NUMERATOR -> {
                        dy = -0.5
                    }
                    Shift.DENOMINATOR -> {
                        dy = 1.0
                    }
                    Shift.FRACTION_BAR -> {
                        shiftsStack.addLast(Shift.FRACTION_BAR)
                        dy = -0.5
                    }
                    Shift.REVERT -> {
                        dy = when (shiftsStack.removeLast()) {
                            Shift.SUBSCRIPT -> -0.4
                            Shift.SUPERSCRIPT -> 0.4
                            Shift.FRACTION_BAR -> null
                            else -> error("Unexpected shift type")
                        }
                    }
                }
            }
            return dy?.let { "${it}em" }
        }

        enum class Shift {
            CURRENT, PASS, SUPERSCRIPT, SUBSCRIPT, NUMERATOR, DENOMINATOR, FRACTION_BAR, REVERT;
        }
    }
}
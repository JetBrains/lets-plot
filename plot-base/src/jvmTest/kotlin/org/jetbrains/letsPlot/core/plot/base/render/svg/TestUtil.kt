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
        level: FormulaLevel = FormulaLevel(),
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

        fun copy(): FormulaLevel = FormulaLevel().apply { shifts.addAll(this@FormulaLevel.shifts) }

        // The level remains unchanged
        fun current(): FormulaLevel = apply { shifts.add(Shift.CURRENT) }

        // No checks
        fun pass(): FormulaLevel = apply { shifts.add(Shift.PASS) }

        fun sup(): FormulaLevel = apply { shifts.add(Shift.SUPERSCRIPT) }

        fun sub(): FormulaLevel = apply { shifts.add(Shift.SUBSCRIPT) }

        fun num(): FormulaLevel = apply { shifts.add(Shift.NUMERATOR) }

        fun denom(): FormulaLevel = apply { shifts.add(Shift.DENOMINATOR) }

        fun bar(): FormulaLevel = apply { shifts.add(Shift.FRACTION_BAR) }

        fun revert(): FormulaLevel = apply { shifts.add(Shift.REVERT) }

        fun toPass(): Boolean = shifts.isEmpty() || shifts.last() == Shift.PASS

        fun sizeValue(): Double? = computeState().size

        fun size(): String? = sizeValue()?.let { "${it}em" }

        fun dy(): String? = computeState().dy?.let { "${it}em" }

        private fun computeState(): FormulaRenderState {
            val shiftsStack = ArrayDeque<Shift>()
            var level = 0
            var size: Double? = null
            var dy: Double? = null
            shifts.forEach { shift ->
                when (shift) {
                    Shift.PASS -> {
                        size = null
                        dy = null
                    }
                    Shift.CURRENT -> {
                        size = sizeByLevel(level)
                        dy = null
                    }
                    Shift.SUPERSCRIPT -> {
                        shiftsStack.addLast(Shift.SUPERSCRIPT)
                        level += 1
                        size = sizeByLevel(level)
                        dy = -0.4
                    }
                    Shift.SUBSCRIPT -> {
                        shiftsStack.addLast(Shift.SUBSCRIPT)
                        level += 1
                        size = sizeByLevel(level)
                        dy = 0.4
                    }
                    Shift.NUMERATOR -> {
                        size = sizeByLevel(level)
                        dy = -0.5
                    }
                    Shift.DENOMINATOR -> {
                        size = sizeByLevel(level)
                        dy = 1.0
                    }
                    Shift.FRACTION_BAR -> {
                        shiftsStack.addLast(Shift.FRACTION_BAR)
                        size = sizeByLevel(level)
                        dy = -0.5
                    }
                    Shift.REVERT -> {
                        size = sizeByLevel(level)
                        when (shiftsStack.removeLast()) {
                            Shift.SUPERSCRIPT -> {
                                level += -1
                                dy = 0.4
                            }
                            Shift.SUBSCRIPT -> {
                                level += -1
                                dy = -0.4
                            }
                            Shift.FRACTION_BAR -> {
                                dy = null
                            }
                            else -> IllegalStateException("Unbalanced shift stack")
                        }
                    }
                }
            }
            return FormulaRenderState(size, dy)
        }

        private fun sizeByLevel(level: Int): Double? = 0.7.pow(level).takeIf { (level > 0) }

        private data class FormulaRenderState(val size: Double?, val dy: Double?)

        enum class Shift {
            CURRENT, PASS, SUPERSCRIPT, SUBSCRIPT, NUMERATOR, DENOMINATOR, FRACTION_BAR, REVERT;
        }
    }
}
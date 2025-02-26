/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.values.Colors.parseColor
import org.jetbrains.letsPlot.datamodel.svg.dom.*

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
        sup: Boolean? = null,
        sub: Boolean? = null,
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

        if (sup == true) {
            assertThat(tspan.textDy().get()).isEqualTo("-0.4em")
            assertThat(tspan.getAttribute(SvgTSpanElement.FONT_SIZE).get()).isEqualTo("0.7em")
        } else if (sup == false) {
            assertThat(tspan.textDy().get()).isNull()
        }

        if (sub == true) {
            assertThat(tspan.textDy().get()).isEqualTo("0.4em")
            assertThat(tspan.getAttribute(SvgTSpanElement.FONT_SIZE).get()).isEqualTo("0.7em")
        } else if (sub == false) {
            assertThat(tspan.textDy().get()).isNull()
        }

        if (color != null) {
            assertThat(tspan.fill().get()).isEqualTo(SvgColors.create(parseColor(color)))
        } else {
            assertThat(tspan.fill().get()).isNull()
        }
    }
}
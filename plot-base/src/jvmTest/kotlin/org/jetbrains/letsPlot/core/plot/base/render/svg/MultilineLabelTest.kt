/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.test.Test

class MultilineLabelTest {
    @Test
    fun setHorizontalAnchorBasic() {
        val text = """text"""
        checkHorizontalAnchor(text, Text.HorizontalAnchor.LEFT, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.MIDDLE, "middle")
        checkHorizontalAnchor(text, Text.HorizontalAnchor.RIGHT, "end")
    }

    @Test
    fun setHorizontalAnchorForFormulaWithFraction() {
        val text = """a+\(\frac{b}{c}\)"""
        checkHorizontalAnchor(text, Text.HorizontalAnchor.LEFT, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.MIDDLE, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.RIGHT, null)
    }

    @Test
    fun setHorizontalAnchorForFormulaWithFractionAfterLink() {
        val text = """<a href="https://example.com">link</a>\(\frac{b}{c}\)"""
        checkHorizontalAnchor(text, Text.HorizontalAnchor.LEFT, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.MIDDLE, null)
        checkHorizontalAnchor(text, Text.HorizontalAnchor.RIGHT, null)
    }

    @Test
    fun setHorizontalAnchorForTwoLinesWithFractionInFirst() {
        val text = "a+\\(\\frac{b}{c}\\)\ntext"
        checkHorizontalAnchors(text, Text.HorizontalAnchor.LEFT, listOf(null, null))
        checkHorizontalAnchors(text, Text.HorizontalAnchor.MIDDLE, listOf(null, "middle"))
        checkHorizontalAnchors(text, Text.HorizontalAnchor.RIGHT, listOf(null, "end"))
    }

    private fun checkHorizontalAnchor(
        text: String,
        horizontalAnchor: Text.HorizontalAnchor,
        expectedAnchor: String?
    ) {
        checkHorizontalAnchors(text, horizontalAnchor, listOf(expectedAnchor))
    }

    private fun checkHorizontalAnchors(
        text: String,
        horizontalAnchor: Text.HorizontalAnchor,
        expectedAnchors: List<String?>
    ) {
        val label = MultilineLabel(text)
        label.setHorizontalAnchor(horizontalAnchor)
        @Suppress("UNCHECKED_CAST")
        val lines = label.rootGroup.children() as List<SvgTextElement>
        assertThat(lines.size).isEqualTo(expectedAnchors.size)
        (lines zip expectedAnchors).forEach { (line, expectedAnchor) ->
            if (expectedAnchor == null) {
                assertThat(line.textAnchor().get()).isNull()
            } else {
                assertThat(line.textAnchor().get()).isEqualTo(expectedAnchor)
            }
        }
    }
}
/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.text

import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement

internal class Hyperlink private constructor(
    private val text: String,
    private val href: String,
) : Term {
    override val visualCharCount: Int = text.length
    override val svg: List<SvgElement> = listOf(
        SvgAElement().apply {
            href().set(href)
            xlinkHref().set(href)
            children().add(
                SvgTSpanElement(text).apply {
                    addClass(RichText.HYPERLINK_ELEMENT_CLASS)
                }
            )
        }
    )

    override fun estimateWidth(font: Font, widthCalculator: (String, Font) -> Double): Double {
        return widthCalculator(text, font)
    }

    companion object {
        private val anchorTagRegex = "<a\\s+[^>]*href=\"(?<href>[^\"]*)\"[^>]*>(?<text>[^<]*)</a>".toRegex()

        fun parse(text: String): List<Term> {
            val links = anchorTagRegex.findAll(text)
                .map { match ->
                    val (href, label) = match.destructured
                    Hyperlink(label, href) to match.range
                }.toList()

            return RichText.fillTextTermGaps(text, links)
        }
    }
}

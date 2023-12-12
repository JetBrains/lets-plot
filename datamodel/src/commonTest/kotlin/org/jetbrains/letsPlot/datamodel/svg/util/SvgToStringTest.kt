/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.util

import org.jetbrains.letsPlot.commons.encoding.UnsupportedRGBEncoder
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SvgToStringTest {
    @Test
    fun tspanShouldNotContainSpacesBetweenElements() {
        // spaces between <tspan> elements also affects the rendering, adding unexpected space between text elements
        val svg = SvgSvgElement().apply {
            children().add(
                SvgTextElement().apply {
                    addTSpan(SvgTSpanElement("1"))
                    addTSpan(SvgTSpanElement("2"))
                    addTSpan(SvgTSpanElement("3"))
                }
            )
        }

        val svgString = SvgToString(UnsupportedRGBEncoder()).render(svg)

        // There should be no spaces between <tspan> elements
        assertEquals(
            """
                |<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                |  <text>
                |    <tspan>1</tspan><tspan>2</tspan><tspan>3</tspan>
                |  </text>
                |</svg>
            """.trimMargin(),
            svgString
        )
    }

    @Test
    fun prettyPrintTest() {
        // TODO: add prebuilt subtree
        val svg = SvgSvgElement().apply {
            children().add(
                SvgGElement().apply {
                    children().add(
                        SvgDefsElement().apply {
                            val clipPathElement = SvgClipPathElement().apply {
                                id().set("asd")
                                children().add(
                                    SvgRectElement().apply {
                                        x().set(0.0)
                                        y().set(0.0)
                                        width().set(200.0)
                                        height().set(200.0)
                                    }
                                )
                            }

                            children().add(clipPathElement)
                        }
                    )
                    children().add(
                        SvgGElement().apply {
                            children().add(
                                SvgTextElement().apply {
                                    addTSpan(SvgTSpanElement("1"))
                                    addTSpan(SvgTSpanElement("2"))
                                    addTSpan(SvgTSpanElement("3"))
                                }
                            )
                        }
                    )
                }
            )
        }

        val svgString = SvgToString(UnsupportedRGBEncoder()).render(svg)

        // There should be no spaces between <tspan> elements
        assertTrue(svgString.contains("<tspan>1</tspan><tspan>2</tspan><tspan>3</tspan>"))
    }
}

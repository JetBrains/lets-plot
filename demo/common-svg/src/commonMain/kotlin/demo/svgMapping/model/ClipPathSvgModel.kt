/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.model

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_TEXT_ANCHOR_MIDDLE

object ClipPathSvgModel {
    fun createModel(): SvgSvgElement {
        val clipRect = DoubleRectangle(0.0, 150.0, 200.0, 80.0)

        val svgRoot = SvgSvgElement(
            width = 200.0,
            height = 400.0
        ).apply {
            children().addAll(listOf(
                SvgDefsElement().apply {
                    children().add(
                        SvgClipPathElement().apply {
                            id().set("myClip")
                            children().add(
                                SvgRectElement(clipRect)
                            )
                        }
                    )
                },
                SvgRectElement(clipRect).apply {
                    stroke().set(SvgColors.RED)
                    strokeWidth().set(2.0)
                    fillOpacity().set(0.0)
                },
                SvgGElement().apply {
                    clipPath().set(SvgIRI("myClip"))
                    setAttribute(SvgGraphicsElement.CLIP_BOUNDS_JFX, clipRect)
                    children().addAll(listOf(
                        SvgRectElement(
                            x = 10.0,
                            y = 100.0,
                            width = 180.0,
                            height = 180.0
                        ).apply {
                            fill().set(SvgColors.ORANGE)
                        },
                        SvgEllipseElement(
                            cx = 100.0,
                            cy = 190.0,
                            rx = 50.0,
                            ry = 50.0
                        ).apply {
                            fill().set(SvgColors.DARK_RED)
                        },
                        SvgTextElement(100.0, 120.0, "If you read this").apply {
                            textAnchor().set(SVG_TEXT_ANCHOR_MIDDLE)
                            fillColor().set(Color.DARK_GREEN)
                        },
                        SvgTextElement(100.0, 140.0, "then clip doesnt work(").apply {
                            textAnchor().set(SVG_TEXT_ANCHOR_MIDDLE)
                            fillColor().set(Color.DARK_GREEN)
                        },
                        SvgTextElement(100.0, 165.0, "Visible text").apply {
                            textAnchor().set(SVG_TEXT_ANCHOR_MIDDLE)
                            fillColor().set(Color.WHITE)
                        },
                    ))
                }
            )
            )
        }

        return svgRoot
    }
}
/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svgMapping.model

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors.NONE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgColors.ORANGE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathData
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransformBuilder

object ReferenceSvgModel {
    fun createModel(): SvgSvgElement = SvgSvgElement(500.0, 500.0).apply {
        val p = SvgPathDataBuilder(false)
            .moveTo(150.0, 375.0, true)
            .verticalLineTo(-100.0)
            .ellipticalArc(100.0, 100.0, 0.0, false, false, -100.0, 100.0)
            .closePath()
            .build()
        path(stroke = ORANGE, fill = NONE, pathData = p, strokeWidth = 2.0) {
            transform().set(SvgTransformBuilder().translate(0.0, -30.0).skewY(20.0).build())
        }
        /*
        g {
            slimG(16) {
                var i = 20.0
                while (i < 400) {
                    slimLine(i, 0.0, i, 200.0, LIGHT_GREEN, 20.0)
                    i += 40
                }

                slimCircle(300.0, 60.0, 50.0, Color.DARK_BLUE, Color.LIGHT_YELLOW, 3.0)
                slimPath(createClosedPathFrom(150.0, 175.0), Color.DARK_GREEN, Color.CYAN, 2.0)
                slimRect(160.0, 50.0, 80.0, 50.0, Color.DARK_MAGENTA, Color.LIGHT_MAGENTA, 1.0)
            }

            style(
                mapOf(
                    "TEXT1" to TextStyle(FontFamily.SERIF.name, FontFace.ITALIC, 15.0, Color.BLUE),
                    "TEXT2" to TextStyle(FontFamily.SERIF.name, FontFace.BOLD, 20.0, Color.RED),
                    "DARK_GREEN" to TextStyle(FontFamily.SERIF.name, FontFace.BOLD, 20.0, Color.DARK_GREEN),
                    "EMC2" to TextStyle(FontFamily.HELVETICA.name, FontFace.BOLD, 22.0, Color.BLUE),
                )
            )

            // Superscript with baseline-shift
            text(x = 300.0, y = 150.0, styleClass = "EMC2") {
                tspan("E=mc")
                tspan("2", baselineShift = "super", fontSize = "75%")
                tspan("with baseline-shift", fontSize = "50%")
            }

            // Subscript with dy
            text(x = 300.0, y = 180.0, styleClass = "EMC2") {
                tspan("E=mc")
                tspan("2", dy = "-0.4em", fontSize = "75%")
                tspan("with dy", fontSize = "50%")
            }

            // Multi-style text
            text(x = 300.0, y = 210.0, fill = ORANGE, styleClass = "DARK_GREEN") {
                tspan("Red", fill = RED)
                tspan("-")
                tspan("brown", fill = BROWN)
                tspan(" green")
            }

            text("Slim elements", x = 30.0, y = 85.0, styleClass = "TEXT1") {
                transform().set(SvgTransformBuilder().rotate(-45.0, 20.0, 100.0).build())
            }

            g {
                var i = 220.0
                while (i < 400) {
                    line(0.0, i, 400.0, i, create(LIGHT_GREEN), 20.0)
                    i += 40
                }
            }

            text("Svg elements", x = 20.0, y = 225.0, styleClass = "TEXT2") {
                stroke().set(CORAL)
                strokeWidth().set(1.0)
            }

            circle(cx = 300.0, cy = 260.0, r = 50.0, fill = LIGHT_PINK)
            rect(x = 160.0, y = 250.0, width = 80.0, height = 50.0, stroke = GRAY, fill = LIGHT_YELLOW) {
                getAttribute(SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE).set(getDashes(4.3, 4.3, 1.0))
            }

            val p = SvgPathDataBuilder(false)
                .moveTo(150.0, 375.0, true)
                .verticalLineTo(-100.0)
                .ellipticalArc(100.0, 100.0, 0.0, false, false, -100.0, 100.0)
                .closePath()
                .build()
            path(stroke = ORANGE, fill = NONE, pathData = p, strokeWidth = 2.0) {
                transform().set(SvgTransformBuilder().translate(0.0, -30.0).skewY(20.0).build())
            }

            path(stroke = ORANGE, fill = NONE, pathData = createUnclosedPathFrom(0.0, 200.0), strokeWidth = 1.5)
            path(fill = LIGHT_BLUE, pathData = createHoledPathFrom(350.0, 350.0))

            g {
                transform().set(
                    SvgTransformBuilder()
                        .translate(100.0, 400.0)
                        .rotate(90.0)
                        .build()
                )
                text("Nested rotated", 20.0, 25.0, styleClass = "TEXT2") {
                    stroke().set(CORAL)
                    strokeWidth().set(1.0)
                }
            }
        }

         */
    }

    private fun createClosedPathFrom(x: Double, y: Double): SvgPathData {
        return SvgPathDataBuilder(false)
            .moveTo(x, y, true)
            .verticalLineTo(-100.0)
            .ellipticalArc(100.0, 100.0, 0.0, false, false, -100.0, 100.0)
            .closePath()
            .build()
    }

    private fun createUnclosedPathFrom(x: Double, y: Double): SvgPathData {
        return SvgPathDataBuilder(true)
            .moveTo(x, y)
            .interpolatePoints(createSawPointsFrom(x, y), SvgPathDataBuilder.Interpolation.LINEAR)
            .build()
    }

    private fun createHoledPathFrom(x: Double, y: Double): SvgPathData {
        return SvgPathDataBuilder(false)
            .moveTo(x, y, true)
            .horizontalLineTo(50.0)
            .verticalLineTo(50.0)
            .horizontalLineTo(-50.0)
            .closePath()
            .moveTo(x + 10, y + 10, true)
            .horizontalLineTo(30.0)
            .verticalLineTo(30.0)
            .horizontalLineTo(-30.0)
            .closePath()
            .build()
    }

    private fun createSawPointsFrom(x: Double, y: Double): List<DoubleVector> {
        val points = mutableListOf<DoubleVector>()
        points.add(DoubleVector(x, y))
        var i = 0.0
        while (i < 400) {
            points.add(DoubleVector(i + 20, y - 10))
            points.add(DoubleVector(i + 40, y))
            points.add(DoubleVector(i + 60, y + 10))
            points.add(DoubleVector(i + 80, y))
            i += 80
        }
        return points
    }

    private fun getDashes(d1: Double, d2: Double, strokeWidth: Double): String {
        val dash1 = d1 * strokeWidth
        val dash2 = d2 * strokeWidth
        return "$dash1,$dash2"
    }
}
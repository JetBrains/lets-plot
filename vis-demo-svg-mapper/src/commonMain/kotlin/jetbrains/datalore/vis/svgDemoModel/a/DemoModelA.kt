/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgDemoModel.a

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_STROKE_DASHARRAY_ATTRIBUTE
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.*

object DemoModelA {

    fun createModel(): SvgGElement {
        val svgRoot = SvgGElement()

        svgRoot.children().add(createSlimGroup())

        val textStyles: Map<String, TextStyle> = mapOf(
            "TEXT1" to TextStyle(
                FontFamily.SERIF.name,
                face = FontFace.ITALIC,
                size = 15.0,
                color = Color.BLUE
            ),
            "TEXT2" to TextStyle(
                FontFamily.SERIF.name,
                face = FontFace.BOLD,
                size = 20.0,
                color = Color.RED
            )
        )
        svgRoot.children().add(createStyleElement(textStyles))

        var text = SvgTextElement(30.0, 85.0, "Slim elements")
        text.addClass("TEXT1")
        SvgUtils.transformRotate(text, -45.0, 20.0, 100.0)
        svgRoot.children().add(text)

        svgRoot.children().add(createHLineGroup())

        text = SvgTextElement(20.0, 225.0, "Svg elements")
        text.addClass("TEXT2")
        text.stroke().set(SvgColors.CORAL)
        text.strokeWidth().set(1.0)
        svgRoot.children().add(text)

        val circle = SvgCircleElement(300.0, 260.0, 50.0)
        circle.fillColor().set(Color.LIGHT_PINK)
        svgRoot.children().add(circle)

        val rect = SvgRectElement(160.0, 250.0, 80.0, 50.0)
        rect.fillColor().set(Color.VERY_LIGHT_YELLOW)
        rect.strokeColor().set(Color.GRAY)
        rect.getAttribute(SVG_STROKE_DASHARRAY_ATTRIBUTE).set(
            getDashes(
                4.3,
                4.3,
                1.0
            )
        )
        svgRoot.children().add(rect)

        var path = SvgPathElement(createClosedPathFrom(150.0, 375.0))
        path.fillColor().set(Color.TRANSPARENT)
        path.strokeColor().set(Color.ORANGE)
        path.strokeWidth().set(2.0)
        path.transform().set(SvgTransformBuilder().translate(0.0, -30.0).skewY(20.0).build())
        svgRoot.children().add(path)

        path = SvgPathElement(createUnclosedPathFrom(0.0, 200.0))
        path.fillColor().set(Color.TRANSPARENT)
        path.strokeColor().set(Color.ORANGE)
        path.strokeWidth().set(1.5)
        svgRoot.children().add(path)

        path = SvgPathElement(createHoledPathFrom(350.0, 350.0))
        path.fillColor().set(Color.LIGHT_BLUE)
        svgRoot.children().add(path)

        return svgRoot
    }

    private fun createStyleElement(textStyles: Map<String, TextStyle>): SvgStyleElement {
        return SvgStyleElement(object : SvgCssResource {
            override fun css(): String {
                return StyleSheet(textStyles, defaultFamily = FontFamily.SERIF.name).toCSS()
            }
        })
    }

    private fun createSlimGroup(): SvgNode {
        val slimGroup = SvgSlimElements.g(14)
//                SvgTransformBuilder().rotate(180.0, 400.0, 200.0).build())      // this breaks demos

        var i = 20.0
        while (i < 400) {
            val line = SvgSlimElements.line(i, 0.0, i, 200.0)
            line.setStroke(Color.LIGHT_GREEN, 1.0)
            line.setStrokeWidth(20.0)
            line.appendTo(slimGroup)
            i += 40
        }

        val ellipse = SvgSlimElements.circle(300.0, 60.0, 50.0)
        ellipse.setFill(Color.LIGHT_YELLOW, 1.0)
        ellipse.setStroke(Color.DARK_BLUE, 1.0)
        ellipse.setStrokeWidth(3.0)
        ellipse.appendTo(slimGroup)

        val path = SvgSlimElements.path(
            createClosedPathFrom(
                150.0,
                175.0
            )
        )
        path.setFill(Color.CYAN, 1.0)
        path.setStroke(Color.DARK_GREEN, 1.0)
        path.setStrokeWidth(2.0)
        path.appendTo(slimGroup)

        val rect = SvgSlimElements.rect(160.0, 50.0, 80.0, 50.0)
        rect.setFill(Color.LIGHT_MAGENTA, 1.0)
        rect.setStroke(Color.DARK_MAGENTA, 1.0)
        rect.setStrokeWidth(1.0)
        rect.appendTo(slimGroup)


        // must be wrapped in `normal` SvgGroup
        val g = SvgGElement()
        g.isPrebuiltSubtree = true
        g.children().add(slimGroup.asDummySvgNode())
        return g
    }

    private fun createHLineGroup(): SvgNode {
        val g = SvgGElement()
        var i = 220.0
        while (i < 400) {
            val line = SvgLineElement(0.0, i, 400.0, i)
            line.strokeColor().set(Color.LIGHT_GREEN)
            line.strokeWidth().set(20.0)
            g.children().add(line)
            i += 40
        }
        return g
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
        val points = ArrayList<DoubleVector>(21)
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
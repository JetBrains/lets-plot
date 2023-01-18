/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.AxisUtil
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeValuesRClassic
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement

open class AxisComponentDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    private fun createModel(): GroupComponent {
        val groupComponent = GroupComponent()

        val background = SvgRectElement(
            LEFT_MARGIN.toDouble(),
            TOP_MARGIN.toDouble(),
            CENTER_SQUARE_SIZE.x,
            CENTER_SQUARE_SIZE.y
        )
        background.fillColor().set(Color.LIGHT_GREEN)
        background.strokeWidth().set(0.0)
        groupComponent.add(background)


        val domainX = DoubleSpan(0.0, 1000.0)
        val domainY = DoubleSpan(0.0, 1000.0)

        val rangeX = DoubleSpan(0.0, CENTER_SQUARE_SIZE.x)
        val rangeY = DoubleSpan(0.0, CENTER_SQUARE_SIZE.y)

        val mapperX = Mappers.linear(domainX, rangeX)
        val mapperY = Mappers.linear(domainY, rangeY)
        var scaleX = Scales.continuousDomain("X", /*mapperX,*/ true)
        var scaleY = Scales.continuousDomain("Y", /*mapperY,*/ true)

        scaleX = ScaleBreaksUtil.withBreaks(scaleX, domainX, 10)
        scaleY = ScaleBreaksUtil.withBreaks(scaleY, domainY, 10)

//        val coord = Coords.create(rangeX, rangeY)
        val coord = Coords.DemoAndTest.create(domainX, domainY, CENTER_SQUARE_SIZE)

        val leftAxis = createAxis(
            CENTER_SQUARE_SIZE.y,
            scaleY,
            mapperY,
            coord,
            Orientation.LEFT
        )
        leftAxis.moveTo(LEFT_MARGIN.toDouble(), TOP_MARGIN.toDouble())
        groupComponent.add(leftAxis.rootGroup)

        val bottomAxis = createAxis(
            CENTER_SQUARE_SIZE.x,
            scaleX,
            mapperX,
            coord,
            Orientation.BOTTOM
        )
        bottomAxis.moveTo(LEFT_MARGIN.toDouble(), TOP_MARGIN + CENTER_SQUARE_SIZE.y)
        groupComponent.add(bottomAxis.rootGroup)

        val rightAxis = createAxis(
            CENTER_SQUARE_SIZE.y,
            scaleY,
            mapperY,
            coord,
            Orientation.RIGHT
        )
        rightAxis.moveTo(LEFT_MARGIN + CENTER_SQUARE_SIZE.x, TOP_MARGIN.toDouble())
        groupComponent.add(rightAxis.rootGroup)

        val topAxis = createAxis(
            CENTER_SQUARE_SIZE.x,
            scaleX,
            mapperX,
            coord,
            Orientation.TOP
        )
        topAxis.moveTo(LEFT_MARGIN.toDouble(), TOP_MARGIN.toDouble())
        groupComponent.add(topAxis.rootGroup)

        return groupComponent
    }

    fun createSvgRoots(): List<SvgSvgElement> {
        val demoModels = listOf(createModel())
        return createSvgRoots(demoModels)
    }

    companion object {
        private val CENTER_SQUARE_SIZE = DoubleVector(500.0, 500.0)
        private const val LEFT_MARGIN = 100
        private const val RIGHT_MARGIN =
            LEFT_MARGIN
        private const val TOP_MARGIN = 50
        private const val BOTTOM_MARGIN =
            TOP_MARGIN

        private val DEMO_BOX_SIZE = DoubleVector(
            CENTER_SQUARE_SIZE.x + LEFT_MARGIN.toDouble() + RIGHT_MARGIN.toDouble(),
            CENTER_SQUARE_SIZE.y + TOP_MARGIN.toDouble() + BOTTOM_MARGIN.toDouble()
        )

        private fun createAxis(
            axisLength: Double,
            scale: Scale,
            scaleMapper: ScaleMapper<Double>,
            coord: CoordinateSystem,
            orientation: Orientation
        ): AxisComponent {
//            val axis = AxisComponent(axisLength, orientation)
//            AxisUtil.setBreaks(axis, scale, coord, orientation.isHorizontal)

//            axis.gridLineColor.set(Color.RED)
//            axis.gridLineWidth.set(Plot.Axis.GRID_LINE_WIDTH)
//            axis.gridLineLength.set(100.0)

            val baselineValues = ThemeValuesRClassic()
            val themeOptions = baselineValues + mapOf(
                ThemeOption.PANEL_GRID to mapOf(ThemeOption.Elem.COLOR to Color.RED)
            )
            val theme = DefaultTheme(themeOptions)

            val axis = AxisComponent(
                length = axisLength,
                orientation = orientation,
                breaksData = AxisUtil.breaksData(
                    scale.getScaleBreaks(), /*scaleMapper, */
                    coord,
                    flipAxis = false,
                    orientation.isHorizontal
                ),
                gridLineLength = 100.0,
                gridLineDistance = 0.0,
                axisTheme = if (orientation.isHorizontal) theme.horizontalAxis(flipAxis = false) else theme.verticalAxis(
                    flipAxis = false
                ),
                gridTheme = if (orientation.isHorizontal) theme.panel().gridX() else theme.panel().gridY()
            )

            return axis
        }
    }
}

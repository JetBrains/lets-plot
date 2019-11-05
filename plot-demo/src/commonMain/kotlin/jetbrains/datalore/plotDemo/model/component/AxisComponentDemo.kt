/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.breaks.ScaleBreaksUtil
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.presentation.Defaults.Plot
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.vis.svg.SvgRectElement
import jetbrains.datalore.vis.svg.SvgSvgElement

open class AxisComponentDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    fun createModel(): GroupComponent {
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


        val domainX = ClosedRange.closed(0.0, 1000.0)
        val domainY = ClosedRange.closed(0.0, 1000.0)

        val rangeX = ClosedRange.closed(0.0, CENTER_SQUARE_SIZE.x)
        val rangeY = ClosedRange.closed(0.0, CENTER_SQUARE_SIZE.y)

        var scaleX = Scales.continuousDomain("X", Mappers.linear(domainX, rangeX), true)
        var scaleY = Scales.continuousDomain("Y", Mappers.linear(domainY, rangeY), true)

        scaleX = ScaleBreaksUtil.withBreaks(scaleX, domainX, 10)
        scaleY = ScaleBreaksUtil.withBreaks(scaleY, domainY, 10)

        val coord = Coords.create(rangeX, rangeY)

        val leftAxis = createAxis(
            CENTER_SQUARE_SIZE.y,
            scaleY,
            coord,
            Orientation.LEFT
        )
        leftAxis.moveTo(LEFT_MARGIN.toDouble(), TOP_MARGIN.toDouble())
        groupComponent.add(leftAxis.rootGroup)

        val bottomAxis = createAxis(
            CENTER_SQUARE_SIZE.x,
            scaleX,
            coord,
            Orientation.BOTTOM
        )
        bottomAxis.moveTo(LEFT_MARGIN.toDouble(), TOP_MARGIN + CENTER_SQUARE_SIZE.y)
        groupComponent.add(bottomAxis.rootGroup)

        val rightAxis = createAxis(
            CENTER_SQUARE_SIZE.y,
            scaleY,
            coord,
            Orientation.RIGHT
        )
        rightAxis.moveTo(LEFT_MARGIN + CENTER_SQUARE_SIZE.x, TOP_MARGIN.toDouble())
        groupComponent.add(rightAxis.rootGroup)

        val topAxis = createAxis(
            CENTER_SQUARE_SIZE.x,
            scaleX,
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
            scale: Scale<Double>,
            coord: CoordinateSystem,
            orientation: jetbrains.datalore.plot.builder.guide.Orientation
        ): jetbrains.datalore.plot.builder.guide.AxisComponent {
            val axis = jetbrains.datalore.plot.builder.guide.AxisComponent(axisLength, orientation)
            jetbrains.datalore.plot.builder.AxisUtil.setBreaks(axis, scale, coord, orientation.isHorizontal)
            axis.gridLineColor.set(Color.RED)
            axis.gridLineWidth.set(Plot.Axis.GRID_LINE_WIDTH)
            axis.gridLineLength.set(100.0)
            return axis
        }
    }
}

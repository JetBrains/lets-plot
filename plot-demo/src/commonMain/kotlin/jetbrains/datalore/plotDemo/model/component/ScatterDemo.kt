/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import jetbrains.datalore.base.gcommon.collect.Ordering
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.geom.PointGeom
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.breaks.QuantizeScale
import jetbrains.datalore.plot.builder.AxisUtil
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.common.color.ColorPalette
import jetbrains.datalore.plot.common.color.ColorScheme
import jetbrains.datalore.plot.common.color.PaletteUtil.schemeColors
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class ScatterDemo : SimpleDemoBase() {

    fun createModels(): List<GroupComponent> {
        return listOf(
            gauss(),
            gaussWithContinuousColor(),
            gaussWithLimitsX()
        )
    }

    private fun gauss(): GroupComponent {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)     // Y
        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        var data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()


        // tmp: layout values
        val leftAxisThickness = 55.0
        val bottomAxisThickness = 25.0
        val plotSize = DoubleVector(demoInnerSize.x - leftAxisThickness, demoInnerSize.y - bottomAxisThickness)
        val plotLeftTop = DoubleVector(leftAxisThickness, 0.0)


        // X scale
        var scaleX = continuousScale("A")
        val domainX = data.range(varA)!!
        val rangeX = plotSize.x
        val mapperX = Mappers.mul(domainX, rangeX)
        scaleX = scaleX.with()
            .mapper(mapperX)
            .breaks(listOf(-200.0, -100.0, 0.0, 100.0, 250.0))
            .labels(listOf("-200", "-100", "0", "100", "250"))
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varB)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)
        scaleY = scaleY.with()
            .mapper(mapperY)
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()

        // coord system
        val coord = Coords.create(MapperUtil.map(domainX, mapperX), MapperUtil.map(domainY, mapperY))

        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX)
        data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY)
        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)

        // Render
        val groupComponent = GroupComponent()

        run {
            // X axis
            val axis = AxisComponent(
                rangeX,
                Orientation.BOTTOM
            )
            AxisUtil.setBreaks(axis, scaleX, coord, horizontal = true)
            AxisUtil.applyTheme(axis, theme.axisX())

            axis.tickLabelHorizontalAnchor.set(TextLabel.HorizontalAnchor.MIDDLE)
            axis.tickLabelVerticalAnchor.set(TextLabel.VerticalAnchor.TOP)
            val xAxisOrigin = DoubleVector(leftAxisThickness, plotSize.y)
            axis.moveTo(xAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }


        run {
            // Y axis
            val axis = AxisComponent(
                rangeY,
                Orientation.LEFT
            )

            AxisUtil.setBreaks(axis, scaleY, coord, horizontal = false)
            AxisUtil.applyTheme(axis, theme.axisY())

            // enable grid-lines
            axis.gridLineLength.set(rangeX)
            axis.tickLabelHorizontalAnchor.set(TextLabel.HorizontalAnchor.RIGHT)
            axis.tickLabelVerticalAnchor.set(TextLabel.VerticalAnchor.CENTER)

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        // points layer
        run {
            val aes = AestheticsBuilder(count)
                .x(AestheticsBuilder.listMapper(aesX, scaleX.mapper))
                .y(AestheticsBuilder.listMapper(aesY, scaleY.mapper))
                .color(constant(Color.RED))
                .shape(constant(NamedShape.SOLID_CIRCLE))
                .size(constant(10.0))
                .build()

            val pos = PositionAdjustments.identity()
            val layer =
                jetbrains.datalore.plot.builder.SvgLayerRenderer(
                    aes,
                    PointGeom(), pos, coord, EMPTY_GEOM_CONTEXT
                )
            layer.moveTo(plotLeftTop)
            groupComponent.add(layer.rootGroup)
        }

        return groupComponent
    }

    private fun gaussWithContinuousColor(): GroupComponent {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)   // Y
        val c = ArrayList<Double>()
        for (i in 0 until count) {
            c.add(a[i] * b[i])
        }

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val varC = DataFrame.Variable("C")
        var data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .putNumeric(varC, c)
            .build()


        // tmp: layout values
        val leftAxisThickness = 55.0
        val bottomAxisThickness = 25.0
        val plotSize = DoubleVector(demoInnerSize.x - leftAxisThickness, demoInnerSize.y - bottomAxisThickness)
        val plotLeftTop = DoubleVector(leftAxisThickness, 0.0)


        // X scale
        var scaleX = continuousScale("A")
        val domainX = data.range(varA)!!
        val rangeX = plotSize.x
        val mapperX = Mappers.mul(domainX, rangeX)
        scaleX = scaleX.with()
            .mapper(mapperX)
            .breaks(listOf(-200.0, -100.0, 0.0, 100.0, 250.0))
            .labels(listOf("-200", "-100", "0", "100", "250"))
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varB)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)
        scaleY = scaleY.with()
            .mapper(mapperY)
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()


        // Color scale
        // see 'OutputColors'
        var scaleColor = Scales.continuousDomain("C", Aes.COLOR)
        run {
            @Suppress("UNCHECKED_CAST")
            val rawC = data.getNumeric(varC) as List<Double>
            val minC = Ordering.natural<Double>().min(rawC)
            val maxC = Ordering.natural<Double>().max(rawC)

            val colorScheme = ColorPalette.Diverging.RdYlBu
            val colorScale = quantizedColorScale(
                colorScheme,
                3,
                minC,
                maxC
            )
            val colors = colorScale.outputValues
            val mappedValuesQuantized = colorScale.domainQuantized
            val mapperColor = { input: Double? ->
                // todo: null color
                var color: Color? = null
                for ((index, range) in mappedValuesQuantized.withIndex()) {
                    if (range.contains(input!!)) {
                        color = colors[index]
                    }
                }
                color ?: throw IllegalArgumentException("Value is outside scale domain (val=$input, scale=C)")
            }

            val breaks = ArrayList<Double>()
            val labels = ArrayList<String>()
            for (range in mappedValuesQuantized) {
                val br = (range.lowerEnd + range.upperEnd) / 2
                breaks.add(br)
                labels.add("" + br)
            }

            scaleColor = scaleColor.with()
                .mapper(mapperColor)
                .breaks(breaks)
                .labels(labels)
                .build()
        }

        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX)
        data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY)
        data = DataFrameUtil.applyTransform(data, varC, Aes.COLOR, scaleColor)

        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)
        val aesColor = data.getNumeric(TransformVar.COLOR)


        // Render
        val groupComponent = GroupComponent()

        // coord system
        val coord = Coords.create(MapperUtil.map(domainX, mapperX), MapperUtil.map(domainY, mapperY))

        run {
            // X axis
            val axis = AxisComponent(
                rangeX,
                Orientation.BOTTOM
            )

            AxisUtil.setBreaks(axis, scaleX, coord, horizontal = true)
            AxisUtil.applyTheme(axis, theme.axisX())

            //      xAxis.tickLabelOffsets.set(info.tickLabelAdditionalOffsets);
            //      xAxis.tickLabelRotationDegree.set(info.tickLabelRotationAngle);
            axis.tickLabelHorizontalAnchor.set(TextLabel.HorizontalAnchor.MIDDLE)
            axis.tickLabelVerticalAnchor.set(TextLabel.VerticalAnchor.TOP)
            val xAxisOrigin = DoubleVector(leftAxisThickness, plotSize.y)
            axis.moveTo(xAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }


        run {
            // Y axis
            val axis = AxisComponent(
                rangeY,
                Orientation.LEFT
            )

            AxisUtil.setBreaks(axis, scaleY, coord, horizontal = false)
            AxisUtil.applyTheme(axis, theme.axisY())

            // enable grid-lines
            axis.gridLineLength.set(rangeX)
            //      yAxis.tickLabelOffsets.set(info.tickLabelAdditionalOffsets);
            //      yAxis.tickLabelRotationDegree.set(info.tickLabelRotationAngle);
            axis.tickLabelHorizontalAnchor.set(TextLabel.HorizontalAnchor.RIGHT)
            axis.tickLabelVerticalAnchor.set(TextLabel.VerticalAnchor.CENTER)
            //      yAxis.tickLabelSmallFont.set(info.tickLabelSmallFont);

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        run {
            // points layer
            val aes = AestheticsBuilder(count)
                .x(AestheticsBuilder.listMapper(aesX, scaleX.mapper))
                .y(AestheticsBuilder.listMapper(aesY, scaleY.mapper))
                .color(AestheticsBuilder.listMapper(aesColor, scaleColor.mapper))
                .shape(constant(NamedShape.SOLID_CIRCLE))
                .size(constant(10.0))
                .build()

            val pos = PositionAdjustments.identity()
            val layer =
                jetbrains.datalore.plot.builder.SvgLayerRenderer(
                    aes,
                    PointGeom(), pos, coord, EMPTY_GEOM_CONTEXT
                )
            layer.moveTo(plotLeftTop)
            groupComponent.add(layer.rootGroup)
        }

        return groupComponent
    }

    private fun gaussWithLimitsX(): GroupComponent {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)   // Y

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        var data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()


        // tmp: layout values
        val leftAxisThickness = 55.0
        val bottomAxisThickness = 25.0
        val plotSize = DoubleVector(demoInnerSize.x - leftAxisThickness, demoInnerSize.y - bottomAxisThickness)
        val plotLeftTop = DoubleVector(leftAxisThickness, 0.0)


        // X scale
        var scaleX = continuousScale("A")
        val domainX = data.range(varA)!!
        val rangeX = plotSize.x
        val mapperX = Mappers.mul(domainX, rangeX)
        scaleX = scaleX.with()
            .mapper(mapperX)
            .breaks(listOf(-100.0, 0.0, 100.0))
            .labels(listOf("-100", "0", "100"))
            .lowerLimit(-100.0)
            .upperLimit(100.0)
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varA)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)

        scaleY = scaleY.with()
            .mapper(mapperY)
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()


        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX)
        data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY)

        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)

        // Render
        val groupComponent = GroupComponent()

        // coord system
        val coord = Coords.create(MapperUtil.map(domainX, mapperX), MapperUtil.map(domainY, mapperY))

        run {
            // X axis
            val axis = AxisComponent(
                rangeX,
                Orientation.BOTTOM
            )

            AxisUtil.setBreaks(axis, scaleX, coord, horizontal = true)
            AxisUtil.applyTheme(axis, theme.axisX())

            // enable grid-lines
            axis.gridLineLength.set(rangeY)

            axis.tickLabelHorizontalAnchor.set(TextLabel.HorizontalAnchor.MIDDLE)
            axis.tickLabelVerticalAnchor.set(TextLabel.VerticalAnchor.TOP)
            val xAxisOrigin = DoubleVector(leftAxisThickness, plotSize.y)
            axis.moveTo(xAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }


        run {
            // Y axis
            val axis = AxisComponent(
                rangeY,
                Orientation.LEFT
            )
//            axis.breaks.set(ScaleUtil.axisBreaks(scaleY, coord, false))
//            axis.labels.set(ScaleUtil.labels(scaleY))
            AxisUtil.setBreaks(axis, scaleY, coord, horizontal = false)

//            axis.lineWidth.set(theme.axisY().lineWidth())
//            axis.tickMarkLength.set(theme.axisY().tickMarkLength())
//            axis.tickMarkPadding.set(theme.axisY().tickMarkPadding())
//            axis.tickMarkWidth.set(theme.axisY().tickMarkWidth())
            AxisUtil.applyTheme(axis, theme.axisY())

            // enable grid-lines
            axis.gridLineLength.set(rangeX)
            axis.tickLabelHorizontalAnchor.set(TextLabel.HorizontalAnchor.RIGHT)
            axis.tickLabelVerticalAnchor.set(TextLabel.VerticalAnchor.CENTER)

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        run {
            // points layer
            val aes = AestheticsBuilder(count)
                .x(AestheticsBuilder.listMapper(aesX, scaleX.mapper))
                .y(AestheticsBuilder.listMapper(aesY, scaleY.mapper))
                .color(constant(Color.DARK_BLUE))
                .shape(constant(NamedShape.SOLID_CIRCLE))
                .size(constant(10.0))
                .build()

            val pos = PositionAdjustments.identity()
            val layer =
                jetbrains.datalore.plot.builder.SvgLayerRenderer(
                    aes,
                    PointGeom(), pos, coord, EMPTY_GEOM_CONTEXT
                )
            layer.moveTo(plotLeftTop)
            groupComponent.add(layer.rootGroup)
        }

        return groupComponent
    }

    private companion object {
        fun continuousScale(name: String): Scale<Double> {
            return Scales.continuousDomainNumericRange(name)
        }

        fun quantizedColorScale(
            colorScheme: ColorScheme,
            colorCount: Int,
            minValue: Double,
            maxValue: Double
        ): QuantizeScale<Color> {
            val colors = schemeColors(colorScheme, colorCount)
            return QuantizeScale<Color>()
                .range(colors)
                .domain(minValue, maxValue)
        }
    }
}

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
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.base.data.TransformVar
import jetbrains.datalore.plot.base.geom.PointGeom
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.breaks.QuantizeScale
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.AxisUtil
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeValuesRClassic
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
//            .mapper(mapperX)
            .breaks(listOf(-200.0, -100.0, 0.0, 100.0, 250.0))
            .labels(listOf("-200", "-100", "0", "100", "250"))
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varB)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)
        scaleY = scaleY.with()
//            .mapper(mapperY)
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()

        // coord system
        val coord = Coords.create(MapperUtil.map(domainX, mapperX), MapperUtil.map(domainY, mapperY))

        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX.transform)
        data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY.transform)
        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)

        // Render
        val groupComponent = GroupComponent()

        val theme = DefaultTheme(ThemeValuesRClassic().values)

        run {
            // X axis
            val axis = AxisComponent(
                length = rangeX,
                orientation = Orientation.BOTTOM,
                breaksData = AxisUtil.breaksData(scaleX.getScaleBreaks(), mapperX, coord, horizontal = true),
                gridLineLength = rangeY,
                axisTheme = theme.axisX(),
                gridTheme = theme.panel().gridX(),
            )

            val xAxisOrigin = DoubleVector(leftAxisThickness, plotSize.y)
            axis.moveTo(xAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }


        run {
            // Y axis
            val axis = AxisComponent(
                length = rangeY,
                orientation = Orientation.LEFT,
                breaksData = AxisUtil.breaksData(scaleY.getScaleBreaks(), mapperY, coord, horizontal = false),
                gridLineLength = rangeX,
                axisTheme = theme.axisY(),
                gridTheme = theme.panel().gridY(),
            )

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        // points layer
        run {
            val aes = AestheticsBuilder(count)
//                .x(AestheticsBuilder.listMapper(aesX, scaleX.mapper))
//                .y(AestheticsBuilder.listMapper(aesY, scaleY.mapper))
                .x(AestheticsBuilder.listMapper(aesX, mapperX))
                .y(AestheticsBuilder.listMapper(aesY, mapperY))
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
//            .mapper(mapperX)
            .breaks(listOf(-200.0, -100.0, 0.0, 100.0, 250.0))
            .labels(listOf("-200", "-100", "0", "100", "250"))
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varB)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)
        scaleY = scaleY.with()
//            .mapper(mapperY)
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()


        // Color scale
        // see 'OutputColors'
//        var scaleColor = Scales.DemoAndTest.continuousDomain("C", Aes.COLOR)

        val (scaleColor, mapperColor) = let {
            @Suppress("UNCHECKED_CAST")
            val rawC = data.getNumeric(varC) as List<Double>
            val minC = Ordering.natural<Double>().min(rawC)
            val maxC = Ordering.natural<Double>().max(rawC)

            val colorScheme = ColorPalette.Diverging.RdYlBu
            val quantizedColorScale = quantizedColorScale(
                colorScheme,
                3,
                minC,
                maxC
            )
            val colors = quantizedColorScale.outputValues
            val mappedValuesQuantized = quantizedColorScale.domainQuantized
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

//            scaleColor = scaleColor.with()
////                .mapper(mapperColor)
//                .breaks(breaks)
//                .labels(labels)
//                .build()

            val scaleColor = Scales.DemoAndTest.continuousDomain("C", Aes.COLOR).with()
//                .mapper(mapperColor)
                .breaks(breaks)
                .labels(labels)
                .build()

            Pair(scaleColor, ScaleMapper.wrap(mapperColor))
        }

        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX.transform)
        data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY.transform)
        data = DataFrameUtil.applyTransform(data, varC, Aes.COLOR, scaleColor.transform)

        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)
        val aesColor = data.getNumeric(TransformVar.COLOR)


        // Render
        val groupComponent = GroupComponent()

        // coord system
        val coord = Coords.create(MapperUtil.map(domainX, mapperX), MapperUtil.map(domainY, mapperY))

        val theme = DefaultTheme(ThemeValuesRClassic().values)

        run {
            // X axis
            val axis = AxisComponent(
                length = rangeX,
                orientation = Orientation.BOTTOM,
                breaksData = AxisUtil.breaksData(scaleX.getScaleBreaks(), mapperX, coord, horizontal = true),
                gridLineLength = rangeY,
                axisTheme = theme.axisX(),
                gridTheme = theme.panel().gridX(),
            )

            val xAxisOrigin = DoubleVector(leftAxisThickness, plotSize.y)
            axis.moveTo(xAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }


        run {
            // Y axis
            val axis = AxisComponent(
                length = rangeY,
                orientation = Orientation.LEFT,
                breaksData = AxisUtil.breaksData(scaleY.getScaleBreaks(), mapperY, coord, horizontal = false),
                gridLineLength = rangeX,
                axisTheme = theme.axisY(),
                gridTheme = theme.panel().gridY(),
            )

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        run {
            // points layer
            val aes = AestheticsBuilder(count)
//                .x(AestheticsBuilder.listMapper(aesX, scaleX.mapper))
//                .y(AestheticsBuilder.listMapper(aesY, scaleY.mapper))
//                .color(AestheticsBuilder.listMapper(aesColor, scaleColor.mapper))
                .x(AestheticsBuilder.listMapper(aesX, mapperX))
                .y(AestheticsBuilder.listMapper(aesY, mapperY))
                .color(AestheticsBuilder.listMapper(aesColor, mapperColor))
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
//            .mapper(mapperX)
            .breaks(listOf(-100.0, 0.0, 100.0))
            .labels(listOf("-100", "0", "100"))
            .continuousTransform(Transforms.continuousWithLimits(Transforms.IDENTITY, Pair(-100.0, 100.0)))
//            .lowerLimit(-100.0)
//            .upperLimit(100.0)

            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varA)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)

        scaleY = scaleY.with()
//            .mapper(mapperY)
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()


        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX.transform)
        data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY.transform)

        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)

        // Render
        val groupComponent = GroupComponent()

        // coord system
        val coord = Coords.create(MapperUtil.map(domainX, mapperX), MapperUtil.map(domainY, mapperY))

        run {
            // X axis
            val axis = AxisComponent(
                length = rangeX,
                orientation = Orientation.BOTTOM,
                breaksData = AxisUtil.breaksData(scaleX.getScaleBreaks(), mapperX, coord, horizontal = true),
                gridLineLength = rangeY,
                axisTheme = theme.axisX(),
                gridTheme = theme.panel().gridX(),
            )

            val xAxisOrigin = DoubleVector(leftAxisThickness, plotSize.y)
            axis.moveTo(xAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }


        run {
            // Y axis
            val axis = AxisComponent(
                length = rangeY,
                orientation = Orientation.LEFT,
                breaksData = AxisUtil.breaksData(scaleY.getScaleBreaks(), mapperY, coord, horizontal = false),
                gridLineLength = rangeX,
                axisTheme = theme.axisY(),
                gridTheme = theme.panel().gridY(),
            )

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        run {
            // points layer
            val aes = AestheticsBuilder(count)
//                .x(AestheticsBuilder.listMapper(aesX, scaleX.mapper))
//                .y(AestheticsBuilder.listMapper(aesY, scaleY.mapper))
                .x(AestheticsBuilder.listMapper(aesX, mapperX))
                .y(AestheticsBuilder.listMapper(aesY, mapperY))
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
            return Scales.DemoAndTest.continuousDomainNumericRange(name)
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

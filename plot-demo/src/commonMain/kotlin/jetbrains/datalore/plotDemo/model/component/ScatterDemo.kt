/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.component

import org.jetbrains.letsPlot.commons.intern.gcommon.collect.Ordering
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.random.RandomGaussian.Companion.normal
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.geom.PointGeom
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.base.scale.Mappers
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.QuantizeScale
import org.jetbrains.letsPlot.core.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.builder.AxisUtil
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeValuesRClassic
import jetbrains.datalore.plot.builder.guide.AxisComponent
import jetbrains.datalore.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.commons.color.ColorPalette
import org.jetbrains.letsPlot.core.commons.color.ColorScheme
import org.jetbrains.letsPlot.core.commons.color.PaletteUtil.schemeColors
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

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
        val a = normal(count, 32, 0.0, 100.0)  // X
        val b = normal(count, 64, 0.0, 50.0)     // Y
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
            .breaks(listOf(-200.0, -100.0, 0.0, 100.0, 250.0))
            .labels(listOf("-200", "-100", "0", "100", "250"))
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varB)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)
        scaleY = scaleY.with()
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()

        // coord system
        val coord = Coords.DemoAndTest.create(domainX, domainY, demoInnerSize)

        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, org.jetbrains.letsPlot.core.plot.base.Aes.X, scaleX.transform)
        data = DataFrameUtil.applyTransform(data, varB, org.jetbrains.letsPlot.core.plot.base.Aes.Y, scaleY.transform)
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
                breaksData = AxisUtil.breaksData(
                    scaleX.getScaleBreaks(), /*mapperX,*/ coord,
                    flipAxis = false,
                    horizontal = true
                ),
                gridLineLength = rangeY,
                gridLineDistance = 0.0,
                axisTheme = theme.horizontalAxis(flipAxis = false),
                gridTheme = theme.panel().gridX()
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
                breaksData = AxisUtil.breaksData(
                    scaleY.getScaleBreaks(), /*mapperY, */coord,
                    flipAxis = false,
                    horizontal = false
                ),
                gridLineLength = rangeX,
                gridLineDistance = 0.0,
                axisTheme = theme.verticalAxis(flipAxis = false),
                gridTheme = theme.panel().gridY()
            )

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        // points layer
        run {
            val aes = AestheticsBuilder(count)
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
        val a = normal(count, 32, 0.0, 100.0)  // X
        val b = normal(count, 64, 0.0, 50.0)   // Y
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
            .breaks(listOf(-200.0, -100.0, 0.0, 100.0, 250.0))
            .labels(listOf("-200", "-100", "0", "100", "250"))
            .build()

        // Y scale
        var scaleY = continuousScale("B")
        val domainY = data.range(varB)!!
        val rangeY = plotSize.y
        val mapperY = Mappers.mul(domainY, rangeY)
        scaleY = scaleY.with()
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

            val scaleColor = Scales.DemoAndTest.continuousDomain("C", org.jetbrains.letsPlot.core.plot.base.Aes.COLOR).with()
//                .mapper(mapperColor)
                .breaks(breaks)
                .labels(labels)
                .build()

            Pair(scaleColor, ScaleMapper.wrap(mapperColor))
        }

        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, org.jetbrains.letsPlot.core.plot.base.Aes.X, scaleX.transform)
        data = DataFrameUtil.applyTransform(data, varB, org.jetbrains.letsPlot.core.plot.base.Aes.Y, scaleY.transform)
        data = DataFrameUtil.applyTransform(data, varC, org.jetbrains.letsPlot.core.plot.base.Aes.COLOR, scaleColor.transform)

        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)
        val aesColor = data.getNumeric(TransformVar.COLOR)


        // Render
        val groupComponent = GroupComponent()

        // coord system
        val coord = Coords.DemoAndTest.create(domainX, domainY, demoInnerSize)

        val theme = DefaultTheme(ThemeValuesRClassic().values)

        run {
            // X axis
            val axis = AxisComponent(
                length = rangeX,
                orientation = Orientation.BOTTOM,
                breaksData = AxisUtil.breaksData(
                    scaleX.getScaleBreaks(), /*mapperX, */coord,
                    flipAxis = false,
                    horizontal = true
                ),
                gridLineLength = rangeY,
                gridLineDistance = 0.0,
                axisTheme = theme.horizontalAxis(flipAxis = false),
                gridTheme = theme.panel().gridX()
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
                breaksData = AxisUtil.breaksData(
                    scaleY.getScaleBreaks(), /*mapperY,*/ coord,
                    flipAxis = false,
                    horizontal = false
                ),
                gridLineLength = rangeX,
                gridLineDistance = 0.0,
                axisTheme = theme.verticalAxis(flipAxis = false),
                gridTheme = theme.panel().gridY()
            )

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        run {
            // points layer
            val aes = AestheticsBuilder(count)
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
        val a = normal(count, 32, 0.0, 100.0)  // X
        val b = normal(count, 64, 0.0, 50.0)   // Y

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
            .breaks(listOf(-120.0, -100.0, -50.0, 0.0, 50.0, 100.0))
            .labels(listOf("-120", "-100", "-50", "0", "50", "100"))
            .build()


        // transform and stat always in this order
        data = DataFrameUtil.applyTransform(data, varA, org.jetbrains.letsPlot.core.plot.base.Aes.X, scaleX.transform)
        data = DataFrameUtil.applyTransform(data, varB, org.jetbrains.letsPlot.core.plot.base.Aes.Y, scaleY.transform)

        val aesX = data.getNumeric(TransformVar.X)
        val aesY = data.getNumeric(TransformVar.Y)

        // Render
        val groupComponent = GroupComponent()

        // coord system
        val coord = Coords.DemoAndTest.create(domainX, domainY, demoInnerSize)

        run {
            // X axis
            val axis = AxisComponent(
                length = rangeX,
                orientation = Orientation.BOTTOM,
                breaksData = AxisUtil.breaksData(
                    scaleX.getScaleBreaks(), /*mapperX,*/ coord,
                    flipAxis = false,
                    horizontal = true
                ),
                gridLineLength = rangeY,
                gridLineDistance = 0.0,
                axisTheme = theme.horizontalAxis(flipAxis = false),
                gridTheme = theme.panel().gridX()
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
                breaksData = AxisUtil.breaksData(
                    scaleY.getScaleBreaks(), /*mapperY, */coord,
                    flipAxis = false,
                    horizontal = false
                ),
                gridLineLength = rangeX,
                gridLineDistance = 0.0,
                axisTheme = theme.verticalAxis(flipAxis = false),
                gridTheme = theme.panel().gridY()
            )

            val yAxisOrigin = DoubleVector(leftAxisThickness, 0.0)
            axis.moveTo(yAxisOrigin)
            groupComponent.add(axis.rootGroup)
        }

        run {
            // points layer
            val aes = AestheticsBuilder(count)
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
        fun continuousScale(name: String): Scale {
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

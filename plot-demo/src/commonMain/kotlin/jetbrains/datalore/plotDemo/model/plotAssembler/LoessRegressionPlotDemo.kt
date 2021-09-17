/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.SmoothStat.Method
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.data.AutoMpg
import jetbrains.datalore.plotDemo.data.Diamonds
import jetbrains.datalore.plotDemo.data.Iris
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

open class LoessRegressionPlotDemo : SimpleDemoBase() {

    fun createPlots(): List<Plot> {
        return listOf(
            createPlot(),
            sinPlot()
        )
    }

    private fun createPlot(): Plot {
        val (scaleByAes, layers) = getLayersMpg()

        val assembler = PlotAssembler.singleTile(scaleByAes, layers, CoordProviders.cartesian(), DefaultTheme())
        assembler.title = "Loess Regression"
        assembler.disableInteractions()
        return assembler.createPlot()
    }

    private fun getLayersIris(): Pair<TypedScaleMap, List<GeomLayer>> {

        // scatter
        val valuesX = Iris.sepalLength.data
        val valuesY = Iris.sepalWidth.data
        val varOrigX = DataFrame.Variable(Iris.sepalLength.name)
        val varOrigY = DataFrame.Variable(Iris.sepalWidth.name)
        val varOrigTarget = DataFrame.Variable(Iris.target.name)
        val data = DataFrame.Builder()
            .putNumeric(varOrigX, valuesX)
            .putNumeric(varOrigY, valuesY)
            .put(varOrigTarget, Iris.target.data)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange(Iris.sepalLength.name),
                Aes.Y to Scales.continuousDomainNumericRange(Iris.sepalWidth.name),
                Aes.COLOR to Scales.pureDiscrete(
                    Iris.target.name,
                    Iris.targetSet,
                    listOf(Color.RED, Color.GREEN, Color.BLUE),
                    Color.GRAY
                )
            )
        )

        val scatterLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(GeomProvider.point())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y
                )
            )
            .addBinding(
                VarBinding(
                    varOrigTarget,
                    Aes.COLOR
                )
            )
            .build(data, scaleByAes)


        // Smooth stat (regression)

        val regressionLineLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.smooth(smoothingMethod = Method.LOESS))
            .geom(GeomProvider.smooth())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y
                )
            )
            .addBinding(
                VarBinding(
                    varOrigTarget,
                    Aes.COLOR
                )
            )
            .build(data, scaleByAes)

        return Pair(scaleByAes, listOf(scatterLayer, regressionLineLayer))
    }

    private fun getLayersMpg(): Pair<TypedScaleMap, List<GeomLayer>> {

        // scatter
        val valuesX = AutoMpg.horsepower.data.map(Int::toDouble)
        val valuesY = AutoMpg.mpg.data
        val varOrigX = DataFrame.Variable(AutoMpg.horsepower.name)
        val varOrigY = DataFrame.Variable(AutoMpg.mpg.name)
        val data = DataFrame.Builder()
            .putNumeric(varOrigX, valuesX)
            .putNumeric(varOrigY, valuesY)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange(AutoMpg.horsepower.name),
                Aes.Y to Scales.continuousDomainNumericRange(AutoMpg.mpg.name)
            )
        )

        val scatterLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(GeomProvider.point())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y
                )
            )
            .build(data, scaleByAes)


        // Smooth stat (regression)

        val regressionLineLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.smooth(smoothingMethod = Method.LOESS))
            .geom(GeomProvider.smooth())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y
                )
            )
            .build(data, scaleByAes)

        return Pair(scaleByAes, listOf(scatterLayer, regressionLineLayer))
    }

    private fun getLayersDiamonds(): Pair<TypedScaleMap, List<GeomLayer>> {

        // scatter
        val valuesX = Diamonds.carat.data
        val valuesY = Diamonds.price.data.map(Int::toDouble)
        val varOrigX = DataFrame.Variable(Diamonds.carat.name)
        val varOrigY = DataFrame.Variable(Diamonds.price.name)
        val varOrigCut = DataFrame.Variable(Diamonds.cut.name)
        val data = DataFrame.Builder()
            .putNumeric(varOrigX, valuesX)
            .putNumeric(varOrigY, valuesY)
            .put(varOrigCut, Diamonds.cut.data)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange(Diamonds.carat.name),
                Aes.Y to Scales.continuousDomainNumericRange(Diamonds.price.name),
                Aes.COLOR to Scales.pureDiscrete(
                    Diamonds.cut.name,
                    Diamonds.cutSet,
                    listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE),
                    Color.GRAY
                )
            )
        )

        val scatterLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(GeomProvider.point())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y
                )
            )
            .addBinding(
                VarBinding(
                    varOrigCut,
                    Aes.COLOR
                )
            )
            .build(data, scaleByAes)


        // Smooth stat (regression)

        val regressionLineLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.smooth(smoothingMethod = Method.LOESS))
            .geom(GeomProvider.smooth())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varOrigX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varOrigY,
                    Aes.Y
                )
            )
            .addBinding(
                VarBinding(
                    varOrigCut,
                    Aes.COLOR
                )
            )
            .build(data, scaleByAes)

        return Pair(scaleByAes, listOf(scatterLayer, regressionLineLayer))
    }

    private fun sinPlot(): Plot {
        val dx = 0.01

        val valuesX = generateSequence(0.0) { it + dx }.takeWhile { it < 3 * PI }.toList()
        val valuesY = valuesX.map(::sin).map { it + Random.nextDouble(-0.6, 0.6) }

        val varX = DataFrame.Variable("x")
        val varY = DataFrame.Variable("y")

        val data = DataFrame.Builder()
            .putNumeric(varX, valuesX)
            .putNumeric(varY, valuesY)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange("x"),
                Aes.Y to Scales.continuousDomainNumericRange("y")
            )
        )

        val scatterLayer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(GeomProvider.point())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varY,
                    Aes.Y
                )
            )
            .addConstantAes(Aes.SHAPE, NamedShape.FILLED_CIRCLE)
            .addConstantAes(Aes.FILL, Color.parseHex("#ffffbf"))
            .addConstantAes(Aes.COLOR, Color.LIGHT_GRAY)
            .build(data, scaleByAes)


        val regressionLayerBuilder = GeomLayerBuilder.demoAndTest()
            .geom(GeomProvider.smooth())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varY,
                    Aes.Y
                )
            )
            .addConstantAes(Aes.SIZE, 2.0)

        val defaultLoessLayer = regressionLayerBuilder
            .stat(Stats.smooth(smoothingMethod = Method.LOESS))
            .addConstantAes(Aes.COLOR, Color.BLUE)
            .build(data, scaleByAes)

        val accurateLoessLayer = regressionLayerBuilder
            .stat(Stats.smooth(smoothingMethod = Method.LOESS, span = 0.3))
            .addConstantAes(Aes.COLOR, Color.DARK_GREEN)
            .build(data, scaleByAes)


        val layers = listOf(scatterLayer, defaultLoessLayer, accurateLoessLayer)
        val assembler = PlotAssembler.singleTile(scaleByAes, layers, CoordProviders.cartesian(), DefaultTheme())
        assembler.title = "loess span=0.5(blue) and span=0.3(green)"
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}
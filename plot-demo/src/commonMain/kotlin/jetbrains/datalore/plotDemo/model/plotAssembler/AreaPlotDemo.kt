/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.Mappers
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plotDemo.data.Iris
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

open class AreaPlotDemo : SimpleDemoBase() {
    fun createPlots(): List<jetbrains.datalore.plot.builder.PlotSvgComponent> {
        return listOf(sepalLength())
    }

    fun sepalLength(): jetbrains.datalore.plot.builder.PlotSvgComponent {
        val xColumn = Iris.sepalLength
        val targetColumn = Iris.target

        val varX = DataFrame.Variable("x")
        val varTarget = DataFrame.Variable("target")
        val data = DataFrame.Builder()
            .putNumeric(varX, xColumn.data.toList())
            .put(varTarget, targetColumn.data.toList())
            .build()

        var scaleColor = Scales.DemoAndTest.pureDiscrete<Color>(
            "Y",
            listOf("Iris-setosa", "Iris-versicolor", "Iris-virginica"),
//            listOf(Color.RED, Color.GREEN, Color.BLUE),
//            Color.BLACK
        )

        var filleMapper = Mappers.discrete(
            scaleColor.transform as DiscreteTransform,
            listOf(Color.RED, Color.GREEN, Color.BLUE),
            Color.BLACK
        )

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.DemoAndTest.continuousDomainNumericRange(xColumn.name),
                Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange(""),
                Aes.FILL to scaleColor
            )
        )

        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
            Aes.FILL to filleMapper
        )

        val layer = GeomLayerBuilder.demoAndTest(GeomProvider.area(), Stats.density())
//            .stat(Stats.density())
//            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.area())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .groupingVar(varTarget)
            .addBinding(
                VarBinding(
                    varX,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    Stats.DENSITY,
                    Aes.Y
                )
            )
            .addBinding(VarBinding(varTarget, Aes.FILL))
            .addConstantAes(Aes.ALPHA, 0.7)
            .build(data, scaleByAes, scaleMappersNP)

        val assembler =
            PlotAssembler.demoAndTest(
                listOf(layer),
                scaleByAes,
                scaleMappersNP,
                CoordProviders.cartesian(),
                DefaultTheme.minimal2()
            )
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}

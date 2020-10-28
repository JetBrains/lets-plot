/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.Plot
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class ScatterPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<Plot> {
        return listOf(
            createPlot()
        )
    }

    private fun createPlot(): Plot {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)   // Y

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.continuousDomainNumericRange("A"),
                Aes.Y to Scales.continuousDomainNumericRange("B")
            )
        )

        val layer = GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.point())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            .addBinding(
                VarBinding(
                    varA,
                    Aes.X
                )
            )
            .addBinding(
                VarBinding(
                    varB,
                    Aes.Y
                )
            )
            .build(data, scaleByAes)

        val assembler = PlotAssembler.singleTile(
            scaleByAes,
            listOf(layer),
            CoordProviders.cartesian(), DefaultTheme()
        )
        assembler.setTitle("Scatter plot")
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}

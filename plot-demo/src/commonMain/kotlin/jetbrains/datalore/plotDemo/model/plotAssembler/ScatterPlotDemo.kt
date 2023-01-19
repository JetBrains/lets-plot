/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class ScatterPlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<PlotSvgComponent> {
        return listOf(
            createPlot()
        )
    }

    private fun createPlot(): PlotSvgComponent {
        val count = 200
        val a = DemoUtil.gauss(count, 32, 0.0, 100.0)  // X
        val b = DemoUtil.gauss(count, 64, 0.0, 50.0)   // Y

        val varA = DataFrame.Variable("A")
        val varB = DataFrame.Variable("B")
        val data = DataFrame.Builder()
            .putNumeric(varA, a)
            .putNumeric(varB, b)
            .build()

        val scaleByAes = mapOf<Aes<*>, Scale>(
            Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("A"),
            Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("B")
        )

        val layer = GeomLayerBuilder.demoAndTest(GeomProvider.point(), Stats.IDENTITY)
//            .stat(Stats.IDENTITY)
//            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.point())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
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
            .build(data, scaleByAes, emptyMap())

        val assembler = PlotAssembler.demoAndTest(
            listOf(layer),
            scaleByAes,
            emptyMap(),
            CoordProviders.cartesian(), theme
        )
        assembler.title = "Scatter plot"
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}

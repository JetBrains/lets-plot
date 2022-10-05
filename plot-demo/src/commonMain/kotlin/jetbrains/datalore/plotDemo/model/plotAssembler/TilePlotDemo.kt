/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotAssembler

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.Scales
import jetbrains.datalore.plot.base.scale.transform.Transforms
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.scale.DefaultMapperProvider
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

open class TilePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.PlotSvgComponent> {
        return listOf(
            createPlot()
        )
    }

    private fun createPlot(): jetbrains.datalore.plot.builder.PlotSvgComponent {
        val valuesX = ArrayList<Double>()
        val valuesY = ArrayList<Double>()
        val valuesV = ArrayList<Double>()
        for (row in 0..3) {
            for (col in 0..9) {
                valuesX.add(col.toDouble())
                valuesY.add(row.toDouble())
                valuesV.add(row.toDouble() + col)
            }
        }

        val varX = DataFrame.Variable("X")
        val varY = DataFrame.Variable("Y")
        val varV = DataFrame.Variable("Value")

        val data = DataFrame.Builder()
            .put(varX, valuesX)
            .put(varY, valuesY)
            .put(varV, valuesV)
            .build()

        val scaleByAes = TypedScaleMap(
            mapOf(
                Aes.X to Scales.DemoAndTest.continuousDomainNumericRange("X"),
                Aes.Y to Scales.DemoAndTest.continuousDomainNumericRange("Y"),
                Aes.FILL to ScaleProviderHelper.createDefault(Aes.FILL).createScale(
                    varV.label,
                    Transforms.IDENTITY,
                    continuousRange = false,
                    guideBreaks = null,
                )
            )
        )
        val scaleMappersNP: Map<Aes<*>, ScaleMapper<*>> = mapOf(
//            Aes.FILL to ScaleProviderHelper.createDefault(Aes.FILL).mapperProvider
            Aes.FILL to DefaultMapperProvider[Aes.FILL]
                .createContinuousMapper(data.range(varV)!!, Transforms.IDENTITY),
        )


        //
        // tiles plot layer
        //
        val tilesLayer = GeomLayerBuilder.demoAndTest(GeomProvider.tile(), Stats.IDENTITY)
//            .stat(Stats.IDENTITY)
//            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.tile())
//            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            //      .addConstantAes(Aes.ALPHA, 0.5)
            .addBinding(VarBinding(varX, Aes.X))
            .addBinding(VarBinding(varY, Aes.Y))
            .addBinding(VarBinding(varV, Aes.FILL))
            .build(data, scaleByAes, scaleMappersNP)

        //
        // Plot
        //
        val assembler = PlotAssembler.singleTile(
            listOf(tilesLayer),
            scaleByAes,
            scaleMappersNP,
            CoordProviders.cartesian(), theme
        )
        assembler.title = "Tile geometry"
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}

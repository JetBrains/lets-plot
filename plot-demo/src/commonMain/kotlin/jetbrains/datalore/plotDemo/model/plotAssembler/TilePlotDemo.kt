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
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.assemble.PlotAssembler
import jetbrains.datalore.plot.builder.assemble.PosProvider
import jetbrains.datalore.plot.builder.assemble.TypedScaleMap
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

open class TilePlotDemo : SimpleDemoBase() {
    override val padding: DoubleVector
        get() = DoubleVector.ZERO

    fun createPlots(): List<jetbrains.datalore.plot.builder.Plot> {
        return listOf(
            createPlot()
        )
    }

    private fun createPlot(): jetbrains.datalore.plot.builder.Plot {
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
                Aes.X to Scales.continuousDomainNumericRange("X"),
                Aes.Y to Scales.continuousDomainNumericRange("Y"),
                Aes.FILL to ScaleProviderHelper.createDefault(Aes.FILL).createScale(
                    varV.label,
                    data.range(varV)!!
                )
            )
        )


        //
        // tiles plot layer
        //
        val tilesLayer = jetbrains.datalore.plot.builder.assemble.GeomLayerBuilder.demoAndTest()
            .stat(Stats.IDENTITY)
            .geom(jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.tile())
            .pos(PosProvider.wrap(PositionAdjustments.identity()))
            //      .addConstantAes(Aes.ALPHA, 0.5)
            .addBinding(VarBinding(varX, Aes.X))
            .addBinding(VarBinding(varY, Aes.Y))
            .addBinding(VarBinding(varV, Aes.FILL))
            .build(data, scaleByAes)

        //
        // Plot
        //
        val assembler = PlotAssembler.singleTile(
            scaleByAes,
            listOf(tilesLayer),
            CoordProviders.cartesian(), DefaultTheme()
        )
        assembler.setTitle("Tile geometry")
        assembler.disableInteractions()
        return assembler.createPlot()
    }
}

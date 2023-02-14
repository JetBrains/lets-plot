/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.PlotSvgComponent
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.server.config.BackendSpecTransformUtil

object DemoAndTest {

    fun createPlot(plotSpec: MutableMap<String, Any>, andBuildComponent: Boolean = true): PlotSvgComponent {
        val plot = createPlot(plotSpec) {
            for (s in it) {
                println("PLOT MESSAGE: $s")
            }
        }
        if (andBuildComponent) {
            plot.ensureBuilt()
        }
        return plot
    }

    private fun createPlot(
        plotSpec: MutableMap<String, Any>,
        computationMessagesHandler: ((List<String>) -> Unit)?
    ): PlotSvgComponent {

        PlotConfig.assertFigSpecOrErrorMessage(plotSpec)

        @Suppress("NAME_SHADOWING")
        val plotSpec = transformPlotSpec(plotSpec)
        if (PlotConfig.isFailure(plotSpec)) {
            val errorMessage = PlotConfig.getErrorMessage(plotSpec)
            throw IllegalArgumentException(errorMessage)
        }

        val config = PlotConfigClientSide.create(plotSpec) { messages ->
            if (computationMessagesHandler != null && messages.isNotEmpty()) {
                computationMessagesHandler(messages)
            }
        }

        val assembler = PlotConfigClientSideUtil.createPlotAssembler(config)
        val layoutInfo = assembler.layoutByOuterSize(Defaults.DEF_PLOT_SIZE)
        return assembler.createPlot(layoutInfo)
    }

    private fun transformPlotSpec(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        var plotSpec = plotSpec
        plotSpec = BackendSpecTransformUtil.processTransform(plotSpec)
        return PlotConfigClientSide.processTransform(plotSpec)
    }


    fun contourDemoData(): Map<String, List<*>> {
        val countX = 20
        val countY = 20

        val mean = DoubleVector(5.0, 5.0)
        val height = 1.0
        val radius = 10.0
        val slop = height / radius
        val x = ArrayList<Double>()
        val y = ArrayList<Double>()
        val z = ArrayList<Double>()
        for (row in 0 until countY) {
            for (col in 0 until countX) {
                val dist = DoubleVector(col.toDouble(), row.toDouble()).subtract(mean).length()
                val v = if (dist >= radius)
                    0.0
                else
                    height - dist * slop

                x.add(col.toDouble())
                y.add(row.toDouble())
                z.add(v)
            }
        }

        val map = HashMap<String, List<*>>()
        map["x"] = x
        map["y"] = y
        map["z"] = z
        return map
    }

    fun getMap(opts: Map<String, Any>, key: String): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        val map = opts[key] as? Map<String, Any>
        return map ?: emptyMap()
    }
}

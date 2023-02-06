/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.vis.svg.SvgSvgElement
import javax.swing.JComponent

object MonolithicAwt {
    fun buildPlotFromRawSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return AwtPlotFactoryUtil.buildPlotFromRawSpecs(
            plotSpec,
            plotSize,
            plotMaxWidth,
            svgComponentFactory, executor,
            computationMessagesHandler
        )
    }

    fun buildPlotFromProcessedSpecs(
        plotSpec: MutableMap<String, Any>,
        plotSize: DoubleVector?,
        plotMaxWidth: Double?,
        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
        executor: (() -> Unit) -> Unit,
        computationMessagesHandler: ((List<String>) -> Unit)
    ): JComponent {
        return AwtPlotFactoryUtil.buildPlotFromProcessedSpecs(
            plotSpec,
            plotSize,
            plotMaxWidth,
            svgComponentFactory, executor,
            computationMessagesHandler
        )
    }


// DO NOT REMOVE THIS FUNCTION !!!

//    fun buildPlotComponent(
//        plotContainer: PlotContainer,
//        svgComponentFactory: (svg: SvgSvgElement) -> JComponent,
//        executor: (() -> Unit) -> Unit
//    ): JComponent {
//        return AwtPlotFactoryUtil.buildPlotComponent(
//            plotContainer,
//            svgComponentFactory, executor
//        )
//    }

    /**
     * Static SVG export
     */
    @Deprecated(
        level = DeprecationLevel.ERROR,
        message = "was replaced with PlotSvgExport.buildSvgImageFromRawSpecs",
        replaceWith = ReplaceWith(
            expression = "PlotSvgExport.buildSvgImageFromRawSpecs(plotSpec, plotSize)",
            imports = ["jetbrains.datalore.plot.PlotSvgExport"]
        )
    )

    fun buildSvgImagesFromRawSpecs(
        @Suppress("UNUSED_PARAMETER") plotSpec: MutableMap<String, Any>,
        @Suppress("UNUSED_PARAMETER") plotSize: DoubleVector?,
        @Suppress("UNUSED_PARAMETER") computationMessagesHandler: ((List<String>) -> Unit)
    ): List<String> {
        UNSUPPORTED("was replaced with PlotSvgExport.buildSvgImageFromRawSpecs")
    }
}

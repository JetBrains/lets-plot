/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.awt.canvas.CanvasPane2
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.SvgCanvasFigure2

open class DefaultPlotComponentProviderCanvas(
    processedSpec: MutableMap<String, Any>,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    svgComponentFactory = SVG_COMPONENT_FACTORY_CANVAS,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    companion object {
        private val LOG = PortableLogging.logger(DefaultPlotComponentProviderCanvas::class)

        private fun browseLink(href: String) {
            try {
                val uri = java.net.URI(href)
                java.awt.Desktop.getDesktop().browse(uri)
            } catch (e: Exception) {
                LOG.info { "Failed to open link: $href (${e.message})" }
            }
        }

        private val SVG_COMPONENT_FACTORY_CANVAS = { svg: SvgSvgElement ->
            CanvasPane2().apply {
                figure = SvgCanvasFigure2(svg).apply {
                    onHrefClick(::browseLink)
                }
            }
        }
    }
}
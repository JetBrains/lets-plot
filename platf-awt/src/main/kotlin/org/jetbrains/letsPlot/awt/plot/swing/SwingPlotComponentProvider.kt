/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.swing

import org.jetbrains.letsPlot.awt.canvas.CanvasComponent
import org.jetbrains.letsPlot.awt.plot.component.PlotSpecComponentProvider
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.SvgCanvasDrawable
import java.awt.Desktop
import java.net.URI

open class SwingPlotComponentProvider(
    processedSpec: MutableMap<String, Any>,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    svgComponentFactory = SVG_COMPONENT_FACTORY,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    companion object {
        private val LOG = PortableLogging.logger(SwingPlotComponentProvider::class)

        private fun browseLink(href: String) {
            try {
                val uri = URI(href)
                Desktop.getDesktop().browse(uri)
            } catch (e: Exception) {
                LOG.info { "Failed to open link: $href (${e.message})" }
            }
        }

        // TODO: Consider removing 'private' modifirer in case somebody needs to use
        // MonolithicAwt.buildPlotFromProcessedSpecs(),
        // or
        // MonolithicAwt.buildPlotFromRawSpecs() with this factory.
        private val SVG_COMPONENT_FACTORY = { svg: SvgSvgElement ->
            CanvasComponent().apply {
                content = SvgCanvasDrawable(svg).apply {
                    onHrefClick(::browseLink)
                }
            }
        }
    }
}
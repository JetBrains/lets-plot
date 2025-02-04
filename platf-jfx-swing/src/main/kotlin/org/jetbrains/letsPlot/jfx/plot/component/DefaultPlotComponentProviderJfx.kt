/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.plot.component

import org.jetbrains.letsPlot.awt.plot.component.PlotSpecComponentProvider
import org.jetbrains.letsPlot.commons.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.jfx.plot.util.SceneMapperJfxPanel

open class DefaultPlotComponentProviderJfx(
    processedSpec: MutableMap<String, Any>,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    svgComponentFactory = SVG_COMPONENT_FACTORY_JFX,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    companion object {
        private val LOG = PortableLogging.logger(DefaultPlotComponentProviderJfx::class)

        private val SVG_COMPONENT_FACTORY_JFX = { svg: SvgSvgElement ->
            SceneMapperJfxPanel(svg, stylesheets = emptyList())
        }
    }
}
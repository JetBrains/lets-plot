/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.jfx

import jetbrains.datalore.base.logging.PortableLogging
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import jetbrains.datalore.vis.swing.PlotSpecComponentProvider
import jetbrains.datalore.vis.swing.SceneMapperJfxPanel
import javax.swing.JComponent
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

open class DefaultPlotComponentProviderJfx(
    processedSpec: MutableMap<String, Any>,
    preserveAspectRatio: Boolean,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : PlotSpecComponentProvider(
    processedSpec = processedSpec,
    preserveAspectRatio = preserveAspectRatio,
    svgComponentFactory = SVG_COMPONENT_FACTORY_JFX,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    /**
     * Override when in in IDEA plugin.
     * Use: JBScrollPane
     */
    override fun createScrollPane(plotComponent: JComponent): JScrollPane {
        return JScrollPane(
            plotComponent,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ).apply {
            border = null
        }
    }

    companion object {
        private val LOG = PortableLogging.logger(DefaultPlotComponentProviderJfx::class)

        private val SVG_COMPONENT_FACTORY_JFX = { svg: SvgSvgElement ->
            SceneMapperJfxPanel(svg, stylesheets = emptyList())
        }
    }
}
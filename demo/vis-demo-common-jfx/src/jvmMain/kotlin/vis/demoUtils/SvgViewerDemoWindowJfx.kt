/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.vis.demoUtils.swing.SvgViewerDemoWindowBase
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.platf.jfx.plot.util.SceneMapperJfxPanel
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent

class SvgViewerDemoWindowJfx(
    title: String,
    svgRoots: List<SvgSvgElement>,
    private val stylesheets: List<String> = emptyList(),
    maxCol: Int = 2,
) : SvgViewerDemoWindowBase(
    title,
    svgRoots = svgRoots,
    maxCol = maxCol,
) {
    override fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val component = SceneMapperJfxPanel(
            svgRoot,
            stylesheets
        )

        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}
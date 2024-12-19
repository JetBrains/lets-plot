/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.batik

import demo.common.utils.swing.SvgViewerDemoWindowBase
import org.jetbrains.letsPlot.batik.plot.util.BatikMapperComponent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent

class SvgViewerDemoWindowBatik(
    title: String,
    svgRoots: List<SvgSvgElement>,
    maxCol: Int = 2,
) : SvgViewerDemoWindowBase(
    "$title (Batik)",
    svgRoots = svgRoots,
    maxCol = maxCol,
) {
    override fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val component = BatikMapperComponent(svgRoot, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}
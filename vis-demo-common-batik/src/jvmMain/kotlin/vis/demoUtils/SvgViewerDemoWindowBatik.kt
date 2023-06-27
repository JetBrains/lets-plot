/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.demoUtils

import jetbrains.datalore.vis.demoUtils.swing.SvgViewerDemoWindowBase
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import jetbrains.datalore.vis.swing.BatikMapperComponent
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent

class SvgViewerDemoWindowBatik(
    title: String,
    svgRoots: List<SvgSvgElement>,
    maxCol: Int = 2,
) : SvgViewerDemoWindowBase(
    title,
    svgRoots = svgRoots,
    maxCol = maxCol,
) {
    override fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val component = BatikMapperComponent(svgRoot, BatikMapperComponent.DEF_MESSAGE_CALLBACK)
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}
/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.common.utils.swing

import demo.common.utils.swingbase.SvgViewerDemoWindowBase
import org.jetbrains.letsPlot.awt.canvas.CanvasComponent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.raster.view.SvgCanvasDrawable
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JComponent

class SvgViewerDemoWindowSwing(
    title: String,
    svgRoots: List<SvgSvgElement>,
    maxCol: Int = 2,
) : SvgViewerDemoWindowBase(
    "$title (Pure Swing)",
    svgRoots = svgRoots,
    maxCol = maxCol,
) {
    override fun createPlotComponent(svgRoot: SvgSvgElement): JComponent {
        val svgCanvasDrawable = SvgCanvasDrawable(svgRoot)
        val component = CanvasComponent(svgCanvasDrawable)
        component.border = BorderFactory.createLineBorder(Color.ORANGE, 1)
        return component
    }
}
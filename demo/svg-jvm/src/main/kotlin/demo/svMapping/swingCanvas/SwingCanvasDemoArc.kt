/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svMapping.swingCanvas

import demo.common.utils.swingCanvas.SvgViewerDemoWindowSwingCanvas
import demo.svgMapping.model.ArcSvgModel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

fun main() {
    val svgGroup = ArcSvgModel.createModel()
    val svgRoot = SvgSvgElement(500.0, 500.0)
    svgRoot.children().add(svgGroup)
    SvgViewerDemoWindowSwingCanvas(
        "ArcSvgModel",
        listOf(svgRoot)
    ).open()
}

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.svMapping.batik

import demo.common.utils.batik.SvgViewerDemoWindowBatik
import demo.svgMapping.model.ReferenceSvgModel
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement

fun main() {
    val svgGroup = ReferenceSvgModel.createModel()
    val svgRoot = SvgSvgElement(500.0, 500.0)
    svgRoot.children().add(svgGroup)
    SvgViewerDemoWindowBatik(
        "ReferenceSvgModel",
        listOf(svgRoot)
    ).open()
}

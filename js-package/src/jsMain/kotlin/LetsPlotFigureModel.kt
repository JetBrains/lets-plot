/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.plot.builder.FigureBuildInfo
import org.w3c.dom.HTMLElement
import sizing.SizingPolicy

/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@OptIn(ExperimentalJsExport::class)
@JsName("LetsPlotFigureModel")
@JsExport
class LetsPlotFigureModel internal constructor(
    private val parentElement: HTMLElement,
    private val sizingPolicy: SizingPolicy,
    private val buildInfo: FigureBuildInfo,
    private var figureRegistration: Registration?,
) {
    fun updateView() {
        figureRegistration?.dispose()
        figureRegistration = null

        val figureSize = buildInfo.bounds.dimension
        val figureNewSize = sizingPolicy.resize(figureSize, parentElement)
        val newBuildInfo = buildInfo.withPreferredSize(figureNewSize)

        figureRegistration = FigureToHtml(newBuildInfo, parentElement).eval()
    }
}
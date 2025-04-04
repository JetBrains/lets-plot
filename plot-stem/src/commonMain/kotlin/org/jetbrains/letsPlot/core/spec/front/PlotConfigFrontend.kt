/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.front

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideKey
import org.jetbrains.letsPlot.core.plot.builder.assemble.GuideOptionsList
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Plot.GUIDES
import org.jetbrains.letsPlot.core.spec.PlotConfigUtil
import org.jetbrains.letsPlot.core.spec.config.PlotConfig
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil.createGuideOptions

class PlotConfigFrontend private constructor(
    opts: Map<String, Any>,
    containerTheme: Theme?
) : PlotConfig(
    opts,
    containerTheme,
    isClientSide = true
) {

    val specId: String? = opts[Option.Plot.SPEC_ID] as? String
    internal val guideOptionsMap: Map<GuideKey, GuideOptionsList>

    internal val xAxisPosition: AxisPosition
    internal val yAxisPosition: AxisPosition

    init {
        guideOptionsMap = createGuideOptions(this.scaleConfigs, getMap(GUIDES), aopConversion)

        xAxisPosition = scaleProviderByAes.getValue(Aes.X).axisPosition
        yAxisPosition = scaleProviderByAes.getValue(Aes.Y).axisPosition
    }

    companion object {
        fun processTransform(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
            // No 'frontend' transform is needed at the moment.
            return plotSpec
        }

        fun create(
            plotSpec: Map<String, Any>,
            containerTheme: Theme? = null,
            computationMessagesHandler: ((List<String>) -> Unit)
        ): PlotConfigFrontend {
            val computationMessages = PlotConfigUtil.findComputationMessages(plotSpec)
            if (computationMessages.isNotEmpty()) {
                computationMessagesHandler(computationMessages)
            }
            return PlotConfigFrontend(plotSpec, containerTheme)
        }
    }
}

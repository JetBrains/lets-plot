/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotMessage

import demo.plot.common.model.plotConfig.BarPlot
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.util.sizing.SizingMode
import org.jetbrains.letsPlot.core.util.sizing.SizingPolicy

fun main() {
    with(BarPlot()) {
        (PlotMessageDemoUtil.show(
            "Computation messages (fixed scaled)",
            withComputationMessages(plotSpecList()),
            containerWidth = 400.0,
            containerHeight = 200.0,
            sizing = SizingPolicy(SizingMode.SCALED, SizingMode.SCALED)
        ))
    }
}

private fun withComputationMessages(plotSpecList: List<MutableMap<String, Any>>): List<MutableMap<String, Any>> {
    return plotSpecList.mapIndexed { index, plotSpecs ->
        val numMessages = index + 1
        plotSpecs[Option.CompMessagesGen.PLOT_FEATURE_NAME] = mapOf(
            Option.CompMessagesGen.NUM_MESSAGES to numMessages * 3
        )
        plotSpecs
    }
}

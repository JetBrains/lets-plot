/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotMessage

import demo.plot.common.model.plotConfig.BarPlot
import org.jetbrains.letsPlot.core.spec.Option

fun main() {
    with(BarPlot()) {
        (PlotMessageDemoUtil.show(
            "Computation messages",
            withComputationMessages(plotSpecList()),
            containerWidth = 400.0
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

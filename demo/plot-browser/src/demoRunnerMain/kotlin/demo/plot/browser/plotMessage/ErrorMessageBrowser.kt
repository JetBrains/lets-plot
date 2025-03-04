/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.browser.plotMessage

import org.jetbrains.letsPlot.core.spec.Option.ErrorGen
import org.jetbrains.letsPlot.core.spec.Option.Meta.KIND
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind

fun main() {
    (PlotMessageDemoUtil.show(
        "Error message",
        plotSpecList(),
        containerWidth = 400.0
    ))
}

private fun plotSpecList(): List<MutableMap<String, Any>> {
    return listOf(
        mutableMapOf(
            KIND to Kind.ERROR_GEN,
            ErrorGen.IS_INTERNAL to true,
            ErrorGen.MESSAGE to "This message is about a fatal internal error!"
        ),
        mutableMapOf(
            KIND to Kind.ERROR_GEN,
            ErrorGen.IS_INTERNAL to false,
            ErrorGen.MESSAGE to "This message is about a NON fatal error!"
        ),
    )
}

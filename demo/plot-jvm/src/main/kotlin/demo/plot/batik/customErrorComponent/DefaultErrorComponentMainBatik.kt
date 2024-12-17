/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.customErrorComponent

import org.jetbrains.letsPlot.batik.plot.component.PlotViewerWindowBatik
import java.awt.Dimension

fun main() {
    // The error message as wos reported here:
    // https://github.com/JetBrains/lets-plot/issues/902
    val message =
        "Can't detect type of pattern 'Let's Talk About Sex (1998) Drama2235One Man's Hero (1999).movieId(mean)' used in string pattern '@{Let's Talk About Sex (1998) Drama2235One Man's Hero (1999).movieId(mean)}'"

    // The spec kind generates an error.
    val rawSpec: MutableMap<String, Any> = mutableMapOf(
        "kind" to "error_gen",
        "is_internal" to true,
        "message" to message
    )

    PlotViewerWindowBatik(
        "Default \"error massage\" component",
        Dimension(900, 700),
        rawSpec,
        preserveAspectRatio = false
    ).open()
}

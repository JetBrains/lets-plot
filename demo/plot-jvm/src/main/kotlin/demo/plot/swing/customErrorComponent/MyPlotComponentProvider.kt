/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swing.customErrorComponent

import org.jetbrains.letsPlot.awt.plot.swing.SwingPlotComponentProvider
import javax.swing.JComponent

class MyPlotComponentProvider(
    processedSpec: MutableMap<String, Any>,
    executor: (() -> Unit) -> Unit,
    computationMessagesHandler: (List<String>) -> Unit
) : SwingPlotComponentProvider(
    processedSpec = processedSpec,
    executor = executor,
    computationMessagesHandler = computationMessagesHandler
) {

    override fun createErrorMessageComponent(message: String): JComponent {
        return MyErrorMessageComponent(message)
    }
}
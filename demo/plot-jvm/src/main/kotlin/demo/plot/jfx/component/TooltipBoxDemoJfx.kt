/*
 * Copyright (c) 2023. JetBrains s.r.o. 
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.component

import demo.common.util.demoUtils.jfx.SvgViewerDemoWindowJfx
import demo.plot.shared.model.component.TooltipBoxDemo
import javafx.application.Platform.runLater
import org.jetbrains.letsPlot.jfx.util.runOnFxThread
import java.awt.EventQueue.invokeLater

fun main() {
    with(TooltipBoxDemo()) {
        val models = createModels()
        SvgViewerDemoWindowJfx(
            "Tooltip box",
            createSvgRoots(models.map { it.first })
        ).open()

        // TODO: Fix hack. Wait for attach - TooltipBox uses SvgPeer not available before.
        runOnFxThread {
            invokeLater {
                runLater {
                    models.forEach { runLater { it.second() } }
                }
            }
        }
    }

}

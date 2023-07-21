/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import javafx.application.Platform.runLater
import jetbrains.datalore.plotDemo.model.component.TooltipBoxDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowJfx
import org.jetbrains.letsPlot.platf.jfx.util.runOnFxThread
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
                    models.forEach { it.second() }
                }
            }
        }
    }

}

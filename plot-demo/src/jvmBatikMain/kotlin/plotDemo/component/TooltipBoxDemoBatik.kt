/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.component

import jetbrains.datalore.plotDemo.model.component.TooltipBoxDemo
import jetbrains.datalore.vis.demoUtils.SvgViewerDemoWindowBatik
import java.awt.EventQueue.invokeLater

fun main() {
    with(TooltipBoxDemo()) {
        val models = createModels()
        SvgViewerDemoWindowBatik(
            "Tooltip box",
            createSvgRoots(models.map { it.first })
        ).open()

        // TODO: Fix hack. Wait for attach - TooltipBox uses SvgPeer not available before.
        invokeLater {
            models.forEach { it.second() }
        }
    }
}

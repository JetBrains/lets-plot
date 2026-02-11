/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.swingCanvas.component

import demo.common.utils.swingCanvas.SvgViewerDemoWindowCanvas
import demo.plot.shared.model.component.TooltipBoxDemo
import java.awt.EventQueue.invokeLater

fun main() {
    with(TooltipBoxDemo()) {
        //val models = listOf(createModels()[3])
        val models = createModels()
        SvgViewerDemoWindowCanvas(
            "Tooltip box",
            createSvgRoots(models.map { it.first })
        ).open()

        // TODO: Fix hack. Wait for attach - TooltipBox uses SvgPeer not available before.
        invokeLater {
            models.forEach { it.second() }
        }
    }
}
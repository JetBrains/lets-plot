package demo.plot.swingCanvas.plotConfig

import demo.common.utils.swingCanvas.SvgViewerDemoWindowSwingCanvas
import demo.plot.shared.model.component.TooltipBoxDemo
import java.awt.EventQueue.invokeLater

fun main() {
    with(TooltipBoxDemo()) {
        val models = listOf(createModels()[3])
        SvgViewerDemoWindowSwingCanvas(
            "Tooltip box",
            createSvgRoots(models.map { it.first })
        ).open()

        // TODO: Fix hack. Wait for attach - TooltipBox uses SvgPeer not available before.
        invokeLater {
            models.forEach { it.second() }
        }
    }
}
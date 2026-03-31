package org.jetbrains.letsPlot.awt.plot.component

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.plot.builder.interact.tools.WithFigureModel
import org.jetbrains.letsPlot.core.util.PlotSizeHelper
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JPanel

/**
 * A panel that centers a PlotPanel using manual positioning.
 * This approach works better in environments like tabbed panes where standard layout managers
 * can cause issues with component visibility and repainting.
 *
 * This is an open class to allow extension of com.intellij.openapi.Disposable in plugin code.
 */
open class CenteredPlotPanel(
    private val plotPanel: PlotPanel,
    private val figurePanelDefaultSize: DoubleVector,
) : Disposable,
    WithFigureModel by plotPanel,
    JPanel(null) // null layout for absolute positioning
{

    init {
        isOpaque = true
        background = plotPanel.background
        add(plotPanel)

        preferredSize = java.awt.Dimension(
            figurePanelDefaultSize.x.toInt(),
            figurePanelDefaultSize.y.toInt()
        )

        // Listen for resize events and reposition the plot panel
        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                val containerSize = DoubleVector(width.toDouble(), height.toDouble())
                val plotSize = PlotSizeHelper.figurePanelSizeInContainer(
                    figurePanelDefaultSize = figurePanelDefaultSize,
                    containerSize = containerSize,
                    sizingPolicy = plotPanel.sizingPolicy,
                    hasToolbar = plotPanel.hasToolbar,
                )

                // Apply minimum size constraint
                val constrainedSize = DoubleVector(
                    plotSize.x.coerceAtLeast(MIN_SIZE),
                    plotSize.y.coerceAtLeast(MIN_SIZE)
                )

                // Center the plot panel
                val x = ((width - constrainedSize.x) / 2.0).toInt()
                val y = ((height - constrainedSize.y) / 2.0).toInt()
                plotPanel.setBounds(x, y, constrainedSize.x.toInt(), constrainedSize.y.toInt())
                plotPanel.revalidate()
                plotPanel.repaint()
            }
        })
    }

    override fun dispose() {
        plotPanel.dispose()
        removeAll()
    }

    companion object {
        private const val MIN_SIZE = 100.0
    }
}

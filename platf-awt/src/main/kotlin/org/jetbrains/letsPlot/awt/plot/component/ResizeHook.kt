/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JScrollPane
import javax.swing.Timer

internal class ResizeHook(
    private val plotPanel: PlotPanel,
    skipFirstResizeEvent: Boolean,
    private var plotScrollPane: JScrollPane?,
    private val figureModel: PlotPanelFigureModel,
    private val applicationContext: ApplicationContext,
    repaintDelay: Int // ms

) : ComponentAdapter() {
    private var skipThisRun = skipFirstResizeEvent

    private var lastContainerSize: Dimension? = null

    private val refreshTimer: Timer = Timer(repaintDelay) {
        rebuildPlotComponent()
    }.apply { isRepeats = false }

    override fun componentResized(e: ComponentEvent?) {
        if (!refreshTimer.isRunning && skipThisRun) {
            // When in IDEA pligin we can modify our state
            // only in a command.
            applicationContext.runWriteAction() {
                skipThisRun = false
            }
            return
        }

        refreshTimer.stop()

        if (plotScrollPane != null) {
            plotScrollPane?.preferredSize = e?.component?.size
            plotScrollPane?.size = e?.component?.size
            // Do not actually re-build plot component inside scroll pane.
            plotPanel.revalidate()
            return
        }

        // Wait for timer event to rebuild the plot component.
        refreshTimer.restart()
    }

    private fun rebuildPlotComponent() {
        val containerSize = plotPanel.size
        if (containerSize == null) return

        // We don't rebuild plot component on re-size if it's a scroll panel.
        check(plotScrollPane == null) { "Unexpected JScrollPane" }

        // Either updating an existing plot or
        // creating a new plot for a first time.
        if (lastContainerSize != containerSize) {

            // Rebuild plot if it actually has changed in size.
            applicationContext.runWriteAction(Runnable {
                lastContainerSize = containerSize
            })

            figureModel.rebuildPlotComponent(
//                onComponentCreated = { comp -> plotScrollPane = comp },
                expared = {
                    // Other timer is running? Weird but let's wait for the next action.
                    refreshTimer.isRunning
                }
            )
        }
    }
}
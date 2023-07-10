/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.plot.component

import javafx.application.Platform
import org.jetbrains.letsPlot.platf.awt.plot.component.ApplicationContext
import javax.swing.SwingUtilities

class DefaultSwingContextJfx : ApplicationContext {
    override fun runWriteAction(action: Runnable) {
        action.run()
    }

    override fun invokeLater(action: Runnable, expared: () -> Boolean) {
        val exparableAction = Runnable {
            if (!expared()) {
                action.run()
            }
        }

        runInEdt(
            exparableAction,
            canRunImmediately = false
        )
    }

    companion object {
        val JFX_EDT_EXECUTOR = { action: () -> Unit ->
            runInEdt(
                Runnable {
                    action()
                },
                canRunImmediately = true
            )
        }

        private fun runInEdt(action: Runnable, canRunImmediately: Boolean) {
            if (canRunImmediately && Platform.isFxApplicationThread()) {
                action.run()
            } else {
                try {
                    Platform.runLater(action)
                } catch (e: IllegalStateException) {
                    // Likely the "Toolkit not initialized" exception.
                    // Fallback to SwingUtilities
                    if (canRunImmediately && SwingUtilities.isEventDispatchThread()) {
                        action.run()
                    } else {
                        SwingUtilities.invokeLater(action)
                    }
                }
            }
        }
    }
}
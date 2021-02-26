/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.jfx

import javafx.application.Platform
import jetbrains.datalore.vis.swing.ApplicationContext
import javax.swing.SwingUtilities

class DefaultApplicationContextJfx : ApplicationContext {
    override fun runWriteAction(action: Runnable) {
        action.run()
    }

    override fun invokeLater(action: Runnable, expared: () -> Boolean) {
        JFX_EDT_EXECUTOR {
            if (!expared()) {
                action.run()
            }
        }
    }

    companion object {
        internal val JFX_EDT_EXECUTOR = { runnable: () -> Unit ->
            if (Platform.isFxApplicationThread()) {
                runnable.invoke()
            } else {
                try {
                    Platform.runLater(runnable)
                } catch (e: IllegalStateException) {
                    // Likely the "Toolkit not initialized" exception.
                    // Fallback to SwingUtilities
                    SwingUtilities.invokeLater(runnable)
                }
            }
        }

    }
}
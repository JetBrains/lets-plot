/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas.awt

import java.awt.EventQueue

object AwtCanvasControlGlobalSettings {

    // In IDEA plugin 'executor' should use 'ApplicationManager.getApplication().invokeLater()'
    var executor: AwtCanvasControlExecutor = object : AwtCanvasControlExecutor {
        override fun runInEdt(action: () -> Unit) {
            if (EventQueue.isDispatchThread()) {
                action()
            } else {
                EventQueue.invokeLater(action)
            }
        }
    }

    interface AwtCanvasControlExecutor {
        fun runInEdt(action: () -> Unit)
    }
}
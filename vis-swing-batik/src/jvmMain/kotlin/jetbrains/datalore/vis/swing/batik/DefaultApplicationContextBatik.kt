/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing.batik

import jetbrains.datalore.vis.swing.ApplicationContext
import javax.swing.SwingUtilities

class DefaultApplicationContextBatik : ApplicationContext {
    override fun runWriteAction(action: Runnable) {
        action.run()
    }

    override fun invokeLater(action: Runnable, expared: () -> Boolean) {
        SwingUtilities.invokeLater(Runnable {
            if (!expared()) {
                action.run()
            }
        })
    }
}
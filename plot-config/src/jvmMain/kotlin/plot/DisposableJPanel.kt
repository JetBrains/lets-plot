/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.registration.Disposable
import javax.swing.JPanel

class DisposableJPanel : JPanel(null), Disposable {
    private var isDisposed: Boolean = false
    override fun dispose() {
        require(!isDisposed) { "Already disposed." }
        isDisposed = true
        components.forEach {
            // We a expect all children are disposable
            (it as Disposable).dispose()
        }
    }
}
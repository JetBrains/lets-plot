/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.awt.AwtContainerDisposer
import jetbrains.datalore.base.registration.Disposable
import java.awt.LayoutManager
import javax.swing.JPanel

open class DisposableJPanel(layout: LayoutManager?) : JPanel(layout), Disposable {
    private val disposer = AwtContainerDisposer(this)

    override fun dispose() {
        disposer.dispose()
    }
}
/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.platf.awt.AwtContainerDisposer
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposableRegistration
import jetbrains.datalore.base.registration.DisposingHub
import java.awt.LayoutManager
import javax.swing.JPanel

open class DisposableJPanel(
    layout: LayoutManager?
) : JPanel(layout), Disposable, DisposingHub {
    private val disposer = AwtContainerDisposer(this)
    private val registrations = CompositeRegistration()

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        disposer.dispose()
        registrations.dispose()
    }
}
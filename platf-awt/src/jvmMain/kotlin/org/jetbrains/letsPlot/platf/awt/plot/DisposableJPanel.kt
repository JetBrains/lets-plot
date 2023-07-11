/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.plot

import org.jetbrains.letsPlot.platf.awt.util.AwtContainerDisposer
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposableRegistration
import org.jetbrains.letsPlot.commons.registration.DisposingHub
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
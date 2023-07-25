/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.util

import org.jetbrains.letsPlot.commons.registration.Disposable
import java.awt.Container

class AwtContainerDisposer(
    private val container: Container
) : Disposable {
    private var isDisposed: Boolean = false

    override fun dispose() {
        require(!isDisposed) { "Already disposed." }
        isDisposed = true
        container.components.forEach {
            // We expect all children are disposable
            (it as Disposable).dispose()
        }
    }
}
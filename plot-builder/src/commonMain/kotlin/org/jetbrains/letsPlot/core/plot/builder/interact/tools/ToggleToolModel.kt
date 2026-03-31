/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

abstract class ToggleToolModel {
    private var actionHandler: () -> Unit = {}

    abstract fun setState(selected: Boolean)

    fun onAction(handler: () -> Unit) {
        actionHandler = handler
    }

    fun action() {
        actionHandler()
    }
}

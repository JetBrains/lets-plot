/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.interact.tools

class ActionToolModel {
    private var actionHandler: () -> Unit = {}

    fun onAction(handler: () -> Unit) {
        actionHandler = handler
    }

    fun action() {
        actionHandler()
    }
}

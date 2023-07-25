/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.jfx.util

import javafx.application.Platform

fun runOnFxThread(runnable: () -> Unit) {
    if (Platform.isFxApplicationThread()) {
        runnable.invoke()
    } else {
        Platform.runLater(runnable)
    }
}

internal fun assertFxThread() {
    if (!Platform.isFxApplicationThread()) {
        throw IllegalStateException("Not JFX Application Thread ")
    }
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot.component

import java.awt.Color
import javax.swing.JComponent
import javax.swing.JTextArea

internal class DefaultErrorMessageComponent(
    message: String
) : JTextArea() {
    init {
        this.foreground = Color.RED
        this.text = message.chunked(60).joinToString("\n")
    }

    companion object {
        val factory: (String) -> JComponent = { message ->
            DefaultErrorMessageComponent(message)
        }
    }
}
/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.batik.customErrorComponent

import java.awt.Color
import javax.swing.*

class MyErrorMessageComponent(
    errorMessage: String
) : JPanel() {
    init {
        val icon = UIManager.getIcon("OptionPane.errorIcon") as Icon
        add(JLabel(icon))

        val text = JTextArea(
            errorMessage.chunked(60).joinToString("\n")
        ).also {
            it.foreground = Color.MAGENTA
        }

        add(text)
    }
}

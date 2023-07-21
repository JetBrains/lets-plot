/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core

import kotlinx.browser.document
import org.w3c.dom.HTMLTextAreaElement

actual object Clipboard {
    actual fun copy(text: String) {
        val area = document.createElement("textarea") as HTMLTextAreaElement

        area.setAttribute("readonly", "")
        area.style.position = "absolute"
        area.style.left = "-9999px"
        area.value = text
        document.body?.appendChild(area)
        area.select()
        document.execCommand("copy")
        document.body?.removeChild(area)
    }
}
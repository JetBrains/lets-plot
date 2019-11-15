package jetbrains.livemap.ui

import org.w3c.dom.HTMLTextAreaElement
import kotlin.browser.document

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
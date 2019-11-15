package jetbrains.livemap.ui

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual object Clipboard {
    actual fun copy(text: String) {
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
    }
}
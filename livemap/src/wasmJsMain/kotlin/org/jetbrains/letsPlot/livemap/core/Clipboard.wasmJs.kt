package org.jetbrains.letsPlot.livemap.core

// A modern Wasm JS interop function bridging to the Async Clipboard API
@OptIn(ExperimentalWasmJsInterop::class)
private fun writeToClipboard(text: String): Unit = js("window.navigator.clipboard.writeText(text)")

actual object Clipboard {
    actual fun copy(text: String) {
        writeToClipboard(text)
    }
}
package jetbrains.datalore.base.encoding

import kotlin.browser.window

actual object Base64 {
    actual fun decode(s: String): ByteArray {
        val bin = window.atob(s)
        return ByteArray(bin.length) { i -> bin[i].toByte()}
    }
}
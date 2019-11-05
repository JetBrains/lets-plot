/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

import kotlin.browser.window

actual object Base64 {
    actual fun decode(s: String): ByteArray {
        val bin = window.atob(s)
        return ByteArray(bin.length) { i -> bin[i].toByte()}
    }
}
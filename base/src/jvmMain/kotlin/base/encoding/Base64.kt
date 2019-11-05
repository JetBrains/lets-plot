/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

actual object Base64 {
    actual fun decode(s: String): ByteArray {
        // Decoder throws exception on '\n' in src, use MimeDecoder
        return java.util.Base64.getMimeDecoder().decode(s)
    }
}
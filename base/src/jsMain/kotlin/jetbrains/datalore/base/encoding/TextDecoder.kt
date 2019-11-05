/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

actual class TextDecoder actual constructor() {
    actual fun decode(bytes: ByteArray): String {
        return js("new TextDecoder()").decode(bytes)
    }
}
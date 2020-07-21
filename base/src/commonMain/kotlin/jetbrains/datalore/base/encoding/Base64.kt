/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

expect object Base64 {
    fun decode(s: String): ByteArray
    fun encode(data: ByteArray): String
}
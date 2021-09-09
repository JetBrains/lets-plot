/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.encoding

expect object BinaryUtil {
    fun encodeList(l: List<Double?>): String
    fun decodeList(s: String): List<Double>
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.values

fun toPngDataUri(base64EncodedPngImage: String): String {
    return "data:image/png;base64,$base64EncodedPngImage"
}
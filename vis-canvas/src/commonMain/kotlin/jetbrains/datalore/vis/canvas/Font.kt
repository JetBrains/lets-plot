/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.canvas

data class Font(
    val fontStyle: FontStyle = FontStyle.NORMAL,
    val fontWeight: FontWeight = FontWeight.NORMAL,
    val fontSize: Double = DEFAULT_SIZE,
    val fontFamily: String = DEFAULT_FAMILY
) {
    constructor(
        style: FontStyle?,
        weight: FontWeight?,
        size:Double?,
        family: String?
    ): this(
        style ?: FontStyle.NORMAL,
        weight ?: FontWeight.NORMAL,
        size ?: DEFAULT_SIZE,
        family ?: DEFAULT_FAMILY
    )

    companion object {
        const val DEFAULT_SIZE = 10.0
        const val DEFAULT_FAMILY = "serif"
    }
}
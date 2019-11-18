/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles

data class CellKey(val key: String) {
    val length: Int get() = key.length

    override fun toString(): String = key
}
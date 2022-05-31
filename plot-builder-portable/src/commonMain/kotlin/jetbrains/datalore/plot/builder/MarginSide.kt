/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

enum class MarginSide(private val id: String) {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    TOP("TOP"),
    BOTTOM("BOTTOM");

    override fun toString(): String {
        return "MarginSide $id"
    }
}
/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

enum class Orientation(private val myValue: String) {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    TOP("TOP"),
    BOTTOM("BOTTOM");

    val isHorizontal: Boolean
        get() = this == jetbrains.datalore.plot.builder.guide.Orientation.TOP || this == jetbrains.datalore.plot.builder.guide.Orientation.BOTTOM

    override fun toString(): String {
        return "Orientation{" +
                "myValue='" + myValue + '\''.toString() +
                '}'.toString()
    }
}

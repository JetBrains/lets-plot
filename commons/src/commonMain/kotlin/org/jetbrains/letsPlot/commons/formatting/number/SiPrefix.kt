/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

internal enum class SiPrefix(
    val symbol: String,
    val exponentRange: IntRange,
) {
    YOTTA("Y", 24 until 27),
    ZETTA("Z", 21 until 24),
    EXA("E", 18 until 21),
    PETA("P", 15 until 18),
    TERA("T", 12 until 15),
    GIGA("G", 9 until 12),
    MEGA("M", 6 until 9),
    KILO("k", 3 until 6),
    NONE("", 0 until 3),
    MILLI("m", -3 until 0),
    MICRO("µ", -6 until -3),
    NANO("n", -9 until -6),
    PICO("p", -12 until -9),
    FEMTO("f", -15 until -12),
    ATTO("a", -18 until -15),
    ZEPTO("z", -21 until -18),
    YOCTO("y", -24 until -21);

    val baseExponent = exponentRange.first
}
/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.color

import kotlin.math.max
import kotlin.math.min

object ColorPalette {
    private const val MIN_COLOR_SET_SIZE = 3

    private fun colors(scheme: ColorScheme, count: Int): Array<String> {
        @Suppress("NAME_SHADOWING") var count = count
        count = max(MIN_COLOR_SET_SIZE, count)
        count = min(scheme.maxColors, count)
        return scheme.colorSet[count - MIN_COLOR_SET_SIZE]
    }

    private fun maxColorSetSize(scheme: ColorScheme): Int {
        val colorSet = scheme.colorSet
        return colorSet[colorSet.size - 1].size
    }

    enum class Type(private val myPresentation: String) {
        SEQUENTIAL("sequential"),
        DIVERGING("diverging"),
        QUALITATIVE("qualitative");

        override fun toString(): String {
            return myPresentation
        }
    }

    // redundant `final` in overridden members are necessary due to kotlin-native issue:
    // `Not in vtable error` #2865
    // https://github.com/JetBrains/kotlin-native/issues/2865
    @Suppress("RedundantModalityModifier")
    enum class Sequential(private val myPresentation: String, final override val colorSet: Array<Array<String>>) :
        ColorScheme {
        Blues("blues", ColorSets.BLUES),
        BuGn("blue-green", ColorSets.BU_GN),
        BuPu("blue-purple", ColorSets.BU_PU),
        GnBu("green-blue", ColorSets.GN_BU),
        Greens("greens", ColorSets.GREENS),
        Greys("greys", ColorSets.GREYS),
        Oranges("oranges", ColorSets.ORANGES),
        OrRd("orange-red", ColorSets.OR_RD),
        PuBu("purple-blue", ColorSets.PU_BU),
        PuBuGn("purple-blue-green", ColorSets.PU_BU_GN),
        PuRd("purple-red", ColorSets.PU_RD),
        Purples("purples", ColorSets.PURPLES),
        RdPu("red-purple", ColorSets.RD_PU),
        Reds("reds", ColorSets.REDS),
        YlGn("yellow-green", ColorSets.YL_GN),
        YlGnBu("yellow-green-blue", ColorSets.YL_GN_BU),
        YlOrBr("yellow-orange-brown", ColorSets.YL_OR_BR),
        YlOrRd("yellow-orange-red", ColorSets.YL_OR_RD);

        final override val type: Type
            get() = Type.SEQUENTIAL

        final override val maxColors: Int
            get() = maxColorSetSize(this)

        final override fun getColors(count: Int): Array<String> {
            return colors(this, count)
        }

        final override fun toString(): String {
            return myPresentation
        }
    }

    // redundant `final` in overridden members are necessary due to kotlin-native issue:
    // `Not in vtable error` #2865
    // https://github.com/JetBrains/kotlin-native/issues/2865
    @Suppress("RedundantModalityModifier")
    enum class Diverging(private val myPresentation: String, final override val colorSet: Array<Array<String>>) :
        ColorScheme {
        BrBG("brown-blue/green", ColorSets.BR_BG),
        PiYG("pink-yellow/green", ColorSets.PI_YG),
        PRGn("purple/red-green", ColorSets.PR_GN),
        PuOr("purple-orange", ColorSets.PU_OR),
        RdBu("red-blue", ColorSets.RD_BU),
        RdGy("red-grey", ColorSets.RD_GY),
        RdYlBu("red-yellow-blue", ColorSets.RD_YL_BU),
        RdYlGn("red-yellow-green", ColorSets.RD_YL_GN),
        Spectral("spectral", ColorSets.SPECTRAL);

        final override val type: Type
            get() = Type.DIVERGING

        final override val maxColors: Int
            get() = maxColorSetSize(this)

        final override fun getColors(count: Int): Array<String> {
            return colors(this, count)
        }

        final override fun toString(): String {
            return myPresentation
        }
    }

    // redundant `final` in overridden members are necessary due to kotlin-native issue:
    // `Not in vtable error` #2865
    // https://github.com/JetBrains/kotlin-native/issues/2865
    @Suppress("RedundantModalityModifier")
    enum class Qualitative(private val myPresentation: String, final override val colorSet: Array<Array<String>>) :
        ColorScheme {
        Accent("accent", ColorSets.ACCENT),
        Dark2("dark 2", ColorSets.DARK_2),
        Paired("paired", ColorSets.PAIRED),
        Pastel1("pastel 1", ColorSets.PASTEL_1),
        Pastel2("pastel 2", ColorSets.PASTEL_2),
        Set1("set 1", ColorSets.SET_1),
        Set2("set 2", ColorSets.SET_2),
        Set3("set 3", ColorSets.SET_3);

        final override val type: Type
            get() = Type.QUALITATIVE

        final override val maxColors: Int
            get() = maxColorSetSize(this)

        final override fun getColors(count: Int): Array<String> {
            return colors(this, count)
        }

        final override fun toString(): String {
            return myPresentation
        }
    }

}

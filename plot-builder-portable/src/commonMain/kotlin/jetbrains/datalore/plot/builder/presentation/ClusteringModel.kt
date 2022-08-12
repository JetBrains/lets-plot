/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Font


object ClusteringModel {
    // for Lucida Grande, 14, normal

    private val CLUSTERS = mapOf(
        '/' to 0,
        'i' to 0,
        '.' to 0,
        't' to 0,
        ' ' to 0,
        'j' to 0,
        ']' to 0,
        '\\' to 0,
        '[' to 0,
        'l' to 0,
        ',' to 0,
        '\'' to 0,
        ':' to 0,
        ';' to 0,
        '"' to 1,
        '-' to 1,
        'f' to 1,
        'r' to 1,
        '*' to 1,
        'г' to 1,
        '|' to 1,
        '!' to 1,
        'I' to 1,
        ')' to 1,
        '{' to 1,
        '(' to 1,
        '`' to 1,
        '}' to 1,
        '^' to 1,
        'n' to 2,
        'z' to 2,
        'u' to 2,
        'v' to 2,
        'x' to 2,
        'y' to 2,
        'Г' to 2,
        's' to 2,
        'я' to 2,
        'h' to 2,
        'п' to 2,
        'н' to 2,
        'k' to 2,
        'л' to 2,
        'к' to 2,
        'й' to 2,
        'и' to 2,
        'з' to 2,
        'с' to 2,
        'у' to 2,
        'J' to 2,
        'х' to 2,
        'a' to 2,
        'а' to 2,
        'c' to 2,
        'ч' to 2,
        'э' to 2,
        'в' to 2,
        'т' to 2,
        '=' to 3,
        'ц' to 3,
        'ъ' to 3,
        'ь' to 3,
        '>' to 3,
        '?' to 3,
        '_' to 3,
        'о' to 3,
        '~' to 3,
        'е' to 3,
        'д' to 3,
        'б' to 3,
        'У' to 3,
        'Б' to 3,
        'Т' to 3,
        'З' to 3,
        'Л' to 3,
        'р' to 3,
        'К' to 3,
        '<' to 3,
        'q' to 3,
        'e' to 3,
        'b' to 3,
        'Z' to 3,
        'Y' to 3,
        'g' to 3,
        'T' to 3,
        'o' to 3,
        'p' to 3,
        'L' to 3,
        'd' to 3,
        '0' to 3,
        '1' to 3,
        '+' to 3,
        '3' to 3,
        '4' to 3,
        '5' to 3,
        '6' to 3,
        '7' to 3,
        '$' to 3,
        '#' to 3,
        '8' to 3,
        '2' to 3,
        '9' to 3,
        'F' to 3,
        'B' to 4,
        'Ь' to 4,
        'E' to 4,
        'X' to 4,
        'S' to 4,
        'U' to 4,
        'м' to 4,
        'P' to 4,
        'N' to 4,
        'K' to 4,
        'H' to 4,
        'V' to 4,
        '&' to 4,
        'Ч' to 4,
        'A' to 4,
        'Е' to 4,
        'Д' to 4,
        'Н' to 4,
        'В' to 4,
        'П' to 4,
        'Р' to 4,
        'w' to 4,
        'А' to 4,
        'Х' to 4,
        'Q' to 5,
        'ш' to 5,
        'щ' to 5,
        'D' to 5,
        'ы' to 5,
        'M' to 5,
        'C' to 5,
        'O' to 5,
        'ю' to 5,
        'G' to 5,
        'Ц' to 5,
        'Й' to 5,
        'Ф' to 5,
        'Ъ' to 5,
        'С' to 5,
        'И' to 5,
        'Я' to 5,
        'Э' to 5,
        'М' to 5,
        'ж' to 5,
        'О' to 5,
        'R' to 5,
        'W' to 6,
        'Ю' to 6,
        'ф' to 6,
        '@' to 6,
        '%' to 6,
        'Ы' to 6,
        'm' to 6,
        'Щ' to 6,
        'Ш' to 6,
        'Ж' to 6
    )

    private const val DEFAULT_WIDTH = 6.712826547137424
    private val CLUSTER_WIDTH = mapOf(
        0 to 4.961409344767602,
        1 to 6.005937440495767,
        2 to 9.590962162162162,
        3 to 11.007489652453847,
        4 to 13.0,
        5 to 14.072164948453608,
        6 to 16.106983655274888
    )

    private const val BASIC_FONT_SIZE = 14.0
    private fun isBaseFont(font: Font): Boolean {
        return font.family.toString() == "Lucida Grande" && font.size.toDouble() == BASIC_FONT_SIZE
    }


    private fun sumClusters(text: String): Double {
        return text.map {
            val cluster = CLUSTERS[it]
            if (!CLUSTER_WIDTH.containsKey(cluster)) println("No width for cluster $cluster; symbol: '$it'")
            CLUSTER_WIDTH[cluster] ?: DEFAULT_WIDTH
        }.sum()
    }

    fun textDimension(
        text: String,
        font: Font,
        sizeRatio: Double,
        boldRatio: Double,
        italicRatio: Double,
        nonBaseFontRatio: Double,
        nonBaseFontAdditiveError: Double,
    ): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector.ZERO
        }

        val width = sumClusters(text).let {
            var w = it * font.size / BASIC_FONT_SIZE * sizeRatio
            if (font.isBold) w *= boldRatio
            if (font.isItalic) w *= italicRatio
            if (!isBaseFont(font)) w = nonBaseFontRatio * w + nonBaseFontAdditiveError
            w
        }

        return DoubleVector(width, font.size.toDouble())
    }
}
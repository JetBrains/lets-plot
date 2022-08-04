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
        ' ' to 0,
        ';' to 0,
        'г' to 0,
        'I' to 0,
        '}' to 0,
        '|' to 0,
        '{' to 0,
        't' to 0,
        ':' to 0,
        'r' to 0,
        '\\' to 0,
        ']' to 0,
        '^' to 0,
        '`' to 0,
        'l' to 0,
        'j' to 0,
        'i' to 0,
        '[' to 0,
        'к' to 0,
        'f' to 0,
        '/' to 0,
        '.' to 0,
        '*' to 0,
        ')' to 0,
        ',' to 0,
        '(' to 0,
        '\'' to 0,
        '-' to 0,
        '"' to 0,
        'т' to 0,
        '!' to 0,
        'ц' to 1,
        'z' to 1,
        'y' to 1,
        'x' to 1,
        'v' to 1,
        'u' to 1,
        'ъ' to 1,
        's' to 1,
        'k' to 1,
        'q' to 1,
        'э' to 1,
        'p' to 1,
        'ь' to 1,
        'ч' to 1,
        'n' to 1,
        'р' to 1,
        '~' to 1,
        'Б' to 1,
        'п' to 1,
        'о' to 1,
        'н' to 1,
        'л' to 1,
        'у' to 1,
        'х' to 1,
        'й' to 1,
        'и' to 1,
        'з' to 1,
        'с' to 1,
        'е' to 1,
        'в' to 1,
        'h' to 1,
        'а' to 1,
        'У' to 1,
        'Т' to 1,
        'Л' to 1,
        'К' to 1,
        'З' to 1,
        'Г' to 1,
        'д' to 1,
        'б' to 1,
        'o' to 1,
        'e' to 1,
        'F' to 1,
        'g' to 1,
        '?' to 1,
        '>' to 1,
        '=' to 1,
        '<' to 1,
        '9' to 1,
        '8' to 1,
        '7' to 1,
        '6' to 1,
        '5' to 1,
        '4' to 1,
        '3' to 1,
        '2' to 1,
        '1' to 1,
        '0' to 1,
        '+' to 1,
        '$' to 1,
        '#' to 1,
        'J' to 1,
        'L' to 1,
        'я' to 1,
        'Y' to 1,
        'Z' to 1,
        'T' to 1,
        '_' to 1,
        'a' to 1,
        'b' to 1,
        'c' to 1,
        'd' to 1,
        'R' to 2,
        'ж' to 2,
        'V' to 2,
        'A' to 2,
        'S' to 2,
        'w' to 2,
        'B' to 2,
        'U' to 2,
        '@' to 2,
        'м' to 2,
        'А' to 2,
        'X' to 2,
        'C' to 2,
        'ю' to 2,
        'm' to 2,
        'ф' to 2,
        '&' to 2,
        'ш' to 2,
        'щ' to 2,
        '%' to 2,
        'ы' to 2,
        'W' to 2,
        'D' to 2,
        'Я' to 2,
        'Ю' to 2,
        'N' to 2,
        'В' to 2,
        'K' to 2,
        'Д' to 2,
        'Е' to 2,
        'Ж' to 2,
        'O' to 2,
        'И' to 2,
        'Й' to 2,
        'H' to 2,
        'G' to 2,
        'М' to 2,
        'Н' to 2,
        'О' to 2,
        'П' to 2,
        'Р' to 2,
        'С' to 2,
        'P' to 2,
        'E' to 2,
        'Q' to 2,
        'Х' to 2,
        'Ц' to 2,
        'Ч' to 2,
        'Ш' to 2,
        'Щ' to 2,
        'Ъ' to 2,
        'Ы' to 2,
        'Ь' to 2,
        'Э' to 2,
        'M' to 2,
        'Ф' to 2
    )

    private val CLUSTER_WIDTH = mapOf(
        0 to 5.514902631407658,
        1 to 10.34853878455865,
        2 to 14.060676238999537
    )

    private const val BASIC_FONT_SIZE = 14.0
    private fun isBaseFont(font: Font): Boolean {
        return font.family.toString() == "Lucida Grande" && font.size.toDouble() == BASIC_FONT_SIZE
    }


    private fun sumClusters(text: String): Double {
        return text.map {
            val cluster = CLUSTERS[it]
            if (!CLUSTER_WIDTH.containsKey(cluster)) println("No width for cluster $cluster; symbol: '$it'")
            CLUSTER_WIDTH[cluster] ?: 0.0
        }.sum()
    }

    fun textDimension(
        text: String,
        font: Font,
        sizeRatio: Double,
        boldRatio: Double,
        italicRatio: Double,
        nonBaseFontAdditiveError: Double,
    ): DoubleVector {
        if (text.isEmpty()) {
            return DoubleVector.ZERO
        }

        val width = sumClusters(text).let {
            var w = it * font.size / BASIC_FONT_SIZE * sizeRatio
            if (font.isBold) w *= boldRatio
            if (font.isItalic) w *= italicRatio
            if (!isBaseFont(font)) w += nonBaseFontAdditiveError
            w
        }

        return DoubleVector(width, font.size.toDouble())
    }
}
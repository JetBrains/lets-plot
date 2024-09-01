/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

fun wrap(text: String, wrapLength: Int, countLimit: Int = -1): String {
    return text
        .split("\n")
        .map { wrapLine(it, wrapLength, countLimit) }
        .joinToString(separator = "\n") { it }
}

fun wrapLine(text: String, wrapLength: Int, maxLinesCount: Int = -1): String {
    if (wrapLength <= 0 || text.length <= wrapLength) {
        return text
    }

    return text.split(" ")
        .let { words ->
            val lines = mutableListOf(mutableListOf<String>())
            words.forEach { word ->
                val freeSpace =
                    wrapLength - lines.last().let { line -> line.sumOf(String::length) + line.size }
                        .coerceAtMost(wrapLength)
                when {
                    freeSpace >= word.length -> lines.last().add(word)
                    word.length <= wrapLength -> lines.add(mutableListOf(word))
                    else -> {
                        lines.last().takeIf { freeSpace > 0 }?.add(word.take(freeSpace))
                        word.drop(freeSpace)
                            .chunked(wrapLength)
                            .forEach {
                                lines.add(mutableListOf<String>(it))
                            }
                    }
                }
            }
            lines
        }
        .joinToString(separator = "\n", limit = maxLinesCount) {
            it.joinToString(separator = " ")
        }
}
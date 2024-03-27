/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.string

fun wrap(text: String, lengthLimit: Int, countLimit: Int = -1): String {
    if (lengthLimit < 0 || text.length <= lengthLimit || text.contains("\n")) {
        return text
    }

    return text.split(" ")
        .let { words ->
            val lines = mutableListOf(mutableListOf<String>())
            words.forEach { word ->
                val freeSpace =
                    lengthLimit - lines.last().let { line -> line.sumOf(String::length) + line.size }
                        .coerceAtMost(lengthLimit)
                when {
                    freeSpace >= word.length -> lines.last().add(word)
                    word.length <= lengthLimit -> lines.add(mutableListOf(word))
                    else -> {
                        lines.last().takeIf { freeSpace > 0 }?.add(word.take(freeSpace))
                        word.drop(freeSpace)
                            .chunked(lengthLimit)
                            .forEach {
                                lines.add(mutableListOf<String>(it))
                            }
                    }
                }
            }
            lines
        }
        .joinToString(separator = "\n", limit = countLimit) {
            it.joinToString(separator = " ")
        }
}
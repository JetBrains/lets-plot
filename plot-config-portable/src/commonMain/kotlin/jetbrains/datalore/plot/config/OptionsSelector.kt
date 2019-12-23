/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

fun Map<*, *>.select(vararg query: String): Any? {
    val path = query.dropLast(1)
    var current: Map<*, *> = this
    for (part in path) {
        current = current[part]?.let { it as? Map<*, *> } ?: return null
    }
    return current[query.last()]
}


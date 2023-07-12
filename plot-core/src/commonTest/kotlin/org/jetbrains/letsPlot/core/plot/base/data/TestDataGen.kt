/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.data

import org.jetbrains.letsPlot.core.plot.base.DataFrame

fun generateData(rowCount: Int, varNames: Collection<String>): DataFrame {
    val variables = varNames.map { DataFrame.Variable(it) }

    val builder = DataFrame.Builder()
    for (variable in variables) {
        builder.put(variable,
            org.jetbrains.letsPlot.core.plot.base.data.toSerie(
                variable.name,
                org.jetbrains.letsPlot.core.plot.base.data.indices(rowCount)
            )
        )
    }

    return builder.build()
}

fun indices(count: Int): List<Int> {
    return (0 until count).toList()
}

fun toSerie(prefix: String, ints: Collection<Int>): List<*> {
    return ints.map { v -> prefix + v }
}

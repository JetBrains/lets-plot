/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.core.plot.base.util.afterOrientation

interface Stat {
    fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit = {}): DataFrame

    fun normalize(dataAfterStat: DataFrame): DataFrame

    fun consumes(): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>

    fun hasDefaultMapping(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean

    fun getDefaultMapping(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): DataFrame.Variable

    fun getDefaultVariableMappings(yOrientation: Boolean): Map<DataFrame.Variable, org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return org.jetbrains.letsPlot.core.plot.base.Aes.values()
            .filter { hasDefaultMapping(it) }
            .associateBy { getDefaultMapping(it) }
            .mapValues { (_, aes) ->
                aes.afterOrientation(yOrientation)
            }
    }
}

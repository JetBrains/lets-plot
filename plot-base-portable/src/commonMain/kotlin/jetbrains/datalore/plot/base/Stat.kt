/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.plot.base.util.afterOrientation

interface Stat {
    fun apply(data: DataFrame, statCtx: StatContext, messageConsumer: (s: String) -> Unit = {}): DataFrame

    fun normalize(dataAfterStat: DataFrame): DataFrame

    fun consumes(): List<Aes<*>>

    fun hasDefaultMapping(aes: Aes<*>): Boolean

    fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable

    fun getDefaultVariableMappings(yOrientation: Boolean): Map<DataFrame.Variable, Aes<*>> {
        return Aes.values()
            .filter { hasDefaultMapping(it) }
            .associateBy { getDefaultMapping(it) }
            .mapValues { (_, aes) ->
                aes.afterOrientation(yOrientation)
            }
    }
}

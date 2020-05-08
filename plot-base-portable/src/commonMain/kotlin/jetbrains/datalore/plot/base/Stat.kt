/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.function.Consumer

interface Stat {
    fun apply(data: DataFrame, statCtx: StatContext, compMessageConsumer: Consumer<String> = {}): DataFrame

    fun consumes(): List<Aes<*>>

    fun hasDefaultMapping(aes: Aes<*>): Boolean

    fun getDefaultMapping(aes: Aes<*>): DataFrame.Variable
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Scale

interface MappedDataAccess {

    val mappedAes: Set<Aes<*>>

    val scaleByAes: (Aes<*>) -> Scale<*>

    fun isMapped(aes: Aes<*>): Boolean

    fun <T> getMappedData(aes: Aes<T>, index: Int): MappedData<T>

    fun <T> getOriginalValue(aes: Aes<T>, index: Int): Any?

    fun getMappedDataLabel(aes: Aes<*>): String

    fun isMappedDataContinuous(aes: Aes<*>): Boolean

    class MappedData<T>(
        val label: String,
        val value: String,
        val isContinuous: Boolean
    )
}
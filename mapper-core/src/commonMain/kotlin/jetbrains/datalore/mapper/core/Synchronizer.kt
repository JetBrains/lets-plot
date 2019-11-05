/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.core

/**
 * Synchronizer is a reusable part of [Mapper]
 */
interface Synchronizer {

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<Synchronizer>(0)
    }

    fun attach(ctx: SynchronizerContext)
    fun detach()
}
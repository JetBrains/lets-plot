/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

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
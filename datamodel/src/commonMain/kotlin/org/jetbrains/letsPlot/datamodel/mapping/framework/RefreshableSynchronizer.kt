/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework


/**
 * A simple kind of synchronizer which doesn't listen to a model and refreshes its part of output only when
 * the refresh() method is explicitly called.
 */
interface RefreshableSynchronizer : Synchronizer {
    fun refresh()
}
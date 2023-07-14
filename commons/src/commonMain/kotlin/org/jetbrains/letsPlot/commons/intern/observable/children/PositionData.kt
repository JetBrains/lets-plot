/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.children

interface PositionData<ChildT> {
    fun get(): Position<ChildT>
    fun remove()
}
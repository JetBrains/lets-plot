/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.children

interface PositionData<ChildT> {
    fun get(): Position<ChildT>
    fun remove()
}
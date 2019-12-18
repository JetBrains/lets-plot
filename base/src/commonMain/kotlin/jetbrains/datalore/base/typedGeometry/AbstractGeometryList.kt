/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.typedGeometry

open class AbstractGeometryList<T>(private val myGeometry: List<T>) : AbstractList<T>() {
    override fun get(index: Int): T {
        return myGeometry[index]
    }

    override val size: Int
        get() = myGeometry.size
}

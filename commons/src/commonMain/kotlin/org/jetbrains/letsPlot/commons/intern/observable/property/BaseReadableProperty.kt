/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

abstract class BaseReadableProperty<ValueT> :
    ReadableProperty<ValueT> {
    override val propExpr: String = this::class.toString()

    override fun toString(): String {
        return propExpr
    }
}
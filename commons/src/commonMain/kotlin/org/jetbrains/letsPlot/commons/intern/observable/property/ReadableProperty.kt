/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

import org.jetbrains.letsPlot.commons.intern.function.Supplier
import org.jetbrains.letsPlot.commons.intern.observable.event.EventSource

/**
 * An object which gives access to a value stored somewhere as well as ability to listen to changes to it.
 */
interface ReadableProperty<ValueT> : EventSource<PropertyChangeEvent<out ValueT>>, Supplier<ValueT> {
    val propExpr: String
}
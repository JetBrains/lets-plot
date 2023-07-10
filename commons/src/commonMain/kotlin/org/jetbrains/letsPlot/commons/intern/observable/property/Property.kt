/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.intern.observable.property

/**
 * Read/Write property
 */
interface Property<ValueT> : ReadableProperty<ValueT>, WritableProperty<ValueT>
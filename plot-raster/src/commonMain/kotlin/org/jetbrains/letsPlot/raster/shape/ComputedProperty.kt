/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

internal class ComputedProperty<T>(
    private val valueProvider: () -> T,
    private val onPropertyChanged: (KProperty<*>, T, T) -> Unit
) : ReadOnlyProperty<Any, T> {
    private var isDirty: Boolean = false
    private var value: T = valueProvider()
    private var computing = false

    fun invalidate() {
        if (computing) {
            return
        }
        isDirty = true
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (isDirty) {
            computing = true

            isDirty = false
            val oldValue = value
            value = valueProvider()

            if (oldValue != value) {
                onPropertyChanged(property, oldValue, value)
            }

            computing = false
        }
        return value
    }
}
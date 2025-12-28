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
    private var isDirty: Boolean = true
    private var value: T? = null
    private var computing = false

    fun invalidate() {
        if (computing) return // Prevent loops
        isDirty = true
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        // Prevent recursive stack overflow / NPE during initialization
        if (computing) {
            return value ?: error("Circular dependency detected in '${property.name}'.")
        }

        if (isDirty) {
            computing = true
            try {
                val oldValue = value
                val newValue = valueProvider()
                value = newValue
                isDirty = false

                // Only notify if value really changed (and it's not the first run)
                if (oldValue != null && oldValue != newValue) {
                    onPropertyChanged(property, oldValue as T, newValue)
                }
            } finally {
                computing = false
            }
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}
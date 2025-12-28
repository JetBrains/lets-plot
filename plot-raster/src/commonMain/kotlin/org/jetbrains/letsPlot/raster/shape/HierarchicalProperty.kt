/*
 * Copyright (c) 2025 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import kotlin.reflect.KProperty

internal class HierarchicalProperty<T, P>(
    private val owner: Element,
    private val dependencyProperty: KProperty<P>,
    private val localVersionProvider: () -> Int,
    private val compute: (P?) -> T
) {
    private var cache: T? = null
    private var lastParentVersion = -1
    private var lastLocalVersion = -1
    private var lastParent: Element? = null

    var version = 0
        private set

    fun getValue(): T {
        val parent = owner.parent

        val parentDelegate = parent?.getHierarchicalProperty(dependencyProperty)

        // Force Upstream Update (Pull)
        parentDelegate?.getValue()

        // Resolve Versions
        // If no delegate, we assume version 0 (Constant Root)
        val currentParentVersion = parentDelegate?.version ?: 0
        val currentLocalVersion = localVersionProvider()

        // Check Staleness
        if (cache == null ||
            currentParentVersion != lastParentVersion ||
            currentLocalVersion != lastLocalVersion ||
            parent !== lastParent
        ) {

            // Fetch Parent Value
            // If delegate exists, use it. If not, pass null (Root behavior).
            @Suppress("UNCHECKED_CAST")
            val parentValue = parentDelegate?.getValue() as? P

            val newValue = compute(parentValue)

            if (cache != newValue) {
                cache = newValue
                version++
            }

            lastParentVersion = currentParentVersion
            lastLocalVersion = currentLocalVersion
            lastParent = parent
        }
        return cache!!
    }
}

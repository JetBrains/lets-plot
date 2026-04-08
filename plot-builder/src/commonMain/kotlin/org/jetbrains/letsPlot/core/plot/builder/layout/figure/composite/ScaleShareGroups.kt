/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

/**
 * Provides groups of element indices that share scale axes.
 * Used to propagate zoom/pan across plots with shared scales.
 */
interface ScaleShareGroups {
    val hasSharing: Boolean
    fun sharedXGroupOf(sourceIndex: Int, elementCount: Int): List<Int>
    fun sharedYGroupOf(sourceIndex: Int, elementCount: Int): List<Int>
}

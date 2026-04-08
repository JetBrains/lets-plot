/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

class DeckScaleShareGroups(
    private val shareX: Boolean,
    private val shareY: Boolean,
) : ScaleShareGroups {

    override val hasSharing: Boolean
        get() = shareX || shareY

    override fun sharedXGroupOf(sourceIndex: Int, elementCount: Int): List<Int> {
        return if (shareX) List(elementCount) { it } else emptyList()
    }

    override fun sharedYGroupOf(sourceIndex: Int, elementCount: Int): List<Int> {
        return if (shareY) List(elementCount) { it } else emptyList()
    }
}

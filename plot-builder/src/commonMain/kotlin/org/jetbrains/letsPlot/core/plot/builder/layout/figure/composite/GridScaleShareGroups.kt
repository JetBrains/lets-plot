/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.figure.composite

class GridScaleShareGroups(
    private val shareX: ScaleSharePolicy,
    private val shareY: ScaleSharePolicy,
    private val ncols: Int,
) : ScaleShareGroups {

    override val hasSharing: Boolean
        get() = shareX != ScaleSharePolicy.NONE || shareY != ScaleSharePolicy.NONE

    override fun sharedXGroupOf(sourceIndex: Int, elementCount: Int): List<Int> {
        return GridScaleShareUtil.groupOf(sourceIndex, shareX, elementCount, ncols)
    }

    override fun sharedYGroupOf(sourceIndex: Int, elementCount: Int): List<Int> {
        return GridScaleShareUtil.groupOf(sourceIndex, shareY, elementCount, ncols)
    }
}

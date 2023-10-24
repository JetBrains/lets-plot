/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.core.plot.builder.FrameOfReference
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.MarginSide
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutInfo
import org.jetbrains.letsPlot.core.plot.builder.layout.TileLayoutProvider
import org.jetbrains.letsPlot.commons.values.Color

internal class BogusFrameOfReferenceProvider : FrameOfReferenceProvider {
    override val hAxisLabel: String? = null
    override val vAxisLabel: String? = null

    override val flipAxis: Boolean = false

    override fun createTileLayoutProvider(): TileLayoutProvider {
        throw IllegalStateException("Bogus frame of reference provider is not supposed to be used.")
    }

    override fun createTileFrame(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): FrameOfReference {
        return BogusFrameOfReference()
    }

    override fun createMarginalFrames(
        tileLayoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        plotBackground: Color,
        penColor: Color,
        debugDrawing: Boolean
    ): Map<MarginSide, FrameOfReference> {
        return emptyMap()
    }
}
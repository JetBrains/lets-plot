/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.frame

import jetbrains.datalore.plot.builder.FrameOfReference
import jetbrains.datalore.plot.builder.FrameOfReferenceProvider
import jetbrains.datalore.plot.builder.MarginSide
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider

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
        debugDrawing: Boolean
    ): Map<MarginSide, FrameOfReference> {
        return emptyMap()
    }
}
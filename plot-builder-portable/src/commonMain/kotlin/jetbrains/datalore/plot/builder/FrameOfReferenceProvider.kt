/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayoutProvider

interface FrameOfReferenceProvider {
    val hAxisLabel: String?
    val vAxisLabel: String?

    val flipAxis: Boolean

    fun createTileLayoutProvider(): TileLayoutProvider

    fun createTileFrame(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean = false
    ): FrameOfReference

    fun createMarginalFrames(
        tileLayoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): Map<MarginSide, FrameOfReference>
}
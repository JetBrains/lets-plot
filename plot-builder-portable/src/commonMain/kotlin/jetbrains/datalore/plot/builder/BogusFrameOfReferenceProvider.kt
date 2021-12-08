/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo

class BogusFrameOfReferenceProvider : TileFrameOfReferenceProvider {
    override val hAxisLabel: String? = null
    override val vAxisLabel: String? = null

    override val flipAxis: Boolean = false

    override fun createTileLayout(): TileLayout {
        throw IllegalStateException("Bogus frame of reference provider is not supposed to be used.")
    }

    override fun createFrameOfReference(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): TileFrameOfReference {
        return BogusFrameOfReference()
    }
}
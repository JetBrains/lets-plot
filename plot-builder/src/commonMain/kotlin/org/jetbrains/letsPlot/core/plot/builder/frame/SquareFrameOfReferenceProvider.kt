/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReference
import org.jetbrains.letsPlot.core.plot.builder.MarginSide
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.InsideOutTileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.TopDownTileLayout
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition

internal class SquareFrameOfReferenceProvider(
    plotContext: PlotContext,
    hScaleProto: Scale,
    vScaleProto: Scale,
    private val adjustedDomain: DoubleRectangle,
    flipAxis: Boolean,
    hAxisPosition: AxisPosition,
    vAxisPosition: AxisPosition,
    theme: Theme,
    marginsLayout: GeomMarginsLayout,
    domainByMargin: Map<MarginSide, DoubleSpan>
) : FrameOfReferenceProviderBase(
    plotContext,
    hScaleProto,
    vScaleProto,
    flipAxis,
    hAxisPosition,
    vAxisPosition,
    theme,
    marginsLayout,
    domainByMargin,
    isPolar = false
) {

    override fun createTileLayoutProvider(axisLayoutQuad: AxisLayoutQuad): TileLayoutProvider {
        return MyTileLayoutProvider(axisLayoutQuad, adjustedDomain, marginsLayout, theme.panel().inset(), flipAxis)
    }

    override fun createTileFrame(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): FrameOfReference {

        // Below use any of horizontal/vertical axis info in the "quad".
        // ToDo: with non-rectangular coordinates this might not work as axis length (for example) might be different
        // for top and botto, axis.
        val hAxisLayoutInfo = layoutInfo.axisInfos.bottom
            ?: layoutInfo.axisInfos.top
            ?: throw IllegalStateException("No top/bottom axis info.")

        val vAxisLayoutInfo = layoutInfo.axisInfos.left
            ?: layoutInfo.axisInfos.right
            ?: throw IllegalStateException("No left/right axis info.")

        // Set-up scales and coordinate system.
        val client = DoubleVector(
            hAxisLayoutInfo.axisLength,
            vAxisLayoutInfo.axisLength
        )

        val coord = coordProvider.createCoordinateSystem(adjustedDomain, client)

        val tileFrameOfReference = SquareFrameOfReference(
            plotContext,
            hScaleBreaks = hAxisLayoutInfo.axisBreaks,
            vScaleBreaks = vAxisLayoutInfo.axisBreaks,
            adjustedDomain,
            coord,
            layoutInfo,
            marginsLayout,
            theme,
            flipAxis,
        )
        tileFrameOfReference.isDebugDrawing = debugDrawing
        return tileFrameOfReference
    }


    private class MyTileLayoutProvider(
        private val axisLayoutQuad: AxisLayoutQuad,
        adjustedDomain: DoubleRectangle,
        private val marginsLayout: GeomMarginsLayout,
        private val panelInset: Thickness,
        flipAxis: Boolean,
    ) : TileLayoutProvider {
        private val hvDomain = adjustedDomain.flipIf(flipAxis)

        override fun createTopDownTileLayout(): TileLayout {
            return TopDownTileLayout(
                axisLayoutQuad,
                hDomain = hvDomain.xRange(),
                vDomain = hvDomain.yRange(),
                marginsLayout,
                panelInset
            )
        }

        override fun createInsideOutTileLayout(): TileLayout {
            return InsideOutTileLayout(
                axisLayoutQuad,
                hDomain = hvDomain.xRange(),
                vDomain = hvDomain.yRange(),
                marginsLayout
            )
        }
    }
}
/*
 * Copyright (c) 2024. JetBrains s.r.o.
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
import org.jetbrains.letsPlot.core.plot.builder.coord.PolarCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.InsideOutTileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.PolarTileLayout
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition

internal class PolarFrameOfReferenceProvider(
    plotContext: PlotContext,
    hScaleProto: Scale,
    vScaleProto: Scale,
    private val adjustedDomain: DoubleRectangle,
    flipAxis: Boolean,
    theme: Theme,
    marginsLayout: GeomMarginsLayout,
    domainByMargin: Map<MarginSide, DoubleSpan>
) : FrameOfReferenceProviderBase(
    plotContext,
    hScaleProto,
    vScaleProto,
    flipAxis,
    AxisPosition.BOTTOM,
    AxisPosition.LEFT,
    theme,
    marginsLayout,
    domainByMargin,
    isPolar = true
) {

    override fun createTileLayoutProvider(axisLayoutQuad: AxisLayoutQuad): TileLayoutProvider {
        return MyTileLayoutProvider(axisLayoutQuad, adjustedDomain, marginsLayout, theme.panel().inset())
    }

    override fun createTileFrame(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): FrameOfReference {
        @Suppress("NAME_SHADOWING")
        val coordProvider = coordProvider as PolarCoordProvider

        val hAxisLayoutInfo = layoutInfo.axisInfos.bottom
            ?: throw IllegalStateException("Bottom axis info is required for polar coordinate system.")

        val vAxisLayoutInfo = layoutInfo.axisInfos.left
            ?: throw IllegalStateException("Left axis info is required for polar coordinate system.")

        // Set-up scales and coordinate system.
        val client = DoubleVector(
            hAxisLayoutInfo.axisLength,
            vAxisLayoutInfo.axisLength
        )

        val coord = coordProvider.createCoordinateSystem(adjustedDomain, client)

        val gridDomain = coordProvider.gridDomain(adjustedDomain)

        val tileFrameOfReference = PolarFrameOfReference(
            plotContext,
            hScaleBreaks = hAxisLayoutInfo.axisBreaks,
            vScaleBreaks = vAxisLayoutInfo.axisBreaks,
            gridDomain,
            coord,
            layoutInfo,
            marginsLayout,
            theme,
            flipAxis
        )
        tileFrameOfReference.isDebugDrawing = debugDrawing
        return tileFrameOfReference
    }

    private class MyTileLayoutProvider(
        private val axisLayoutQuad: AxisLayoutQuad,
        private val adjustedDomain: DoubleRectangle,
        private val marginsLayout: GeomMarginsLayout,
        private val panelInset: Thickness,
    ) : TileLayoutProvider {
        override fun createTopDownTileLayout(): TileLayout {
            return PolarTileLayout(
                axisLayoutQuad,
                hDomain = adjustedDomain.xRange(),
                vDomain = adjustedDomain.yRange(),
                marginsLayout,
                panelInset
            )
        }

        override fun createInsideOutTileLayout(): TileLayout {
            return InsideOutTileLayout(
                axisLayoutQuad,
                hDomain = adjustedDomain.xRange(),
                vDomain = adjustedDomain.yRange(),
                marginsLayout
            )
        }
    }
}
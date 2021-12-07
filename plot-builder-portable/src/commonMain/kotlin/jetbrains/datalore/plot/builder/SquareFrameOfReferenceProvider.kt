/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.PlotAxisLayout
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo
import jetbrains.datalore.plot.builder.layout.XYPlotTileLayout
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.Theme

class SquareFrameOfReferenceProvider(
    private val hScaleProto: Scale<Double>,
    private val vScaleProto: Scale<Double>,
    hDomain: ClosedRange<Double>,
    vDomain: ClosedRange<Double>,
    override val flipAxis: Boolean,
    private val theme: Theme
) : TileFrameOfReferenceProvider {

    private val hAxisSpec: AxisSpec
    private val vAxisSpec: AxisSpec

    init {
        hAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(hScaleProto),
            hDomain,
            hScaleProto.name,
            theme.axisX()
        )

        vAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(vScaleProto),
            vDomain,
            vScaleProto.name,
            theme.axisY()
        )
    }

    override val hAxisLabel: String? = if (hAxisSpec.theme.showTitle()) hAxisSpec.label else null
    override val vAxisLabel: String? = if (vAxisSpec.theme.showTitle()) vAxisSpec.label else null

    override fun createTileLayout(): TileLayout {
        val hAxisLayout = PlotAxisLayout(
            hAxisSpec.breaksProviderFactory,
            hAxisSpec.theme,
            Orientation.BOTTOM
        )

        val vAxisLayout = PlotAxisLayout(
            vAxisSpec.breaksProviderFactory,
            vAxisSpec.theme,
            Orientation.LEFT
        )

        val hDomain = hAxisSpec.aesRange
        val vDomain = vAxisSpec.aesRange

        return XYPlotTileLayout(hAxisLayout, vAxisLayout, hDomain, vDomain)
    }

    override fun createFrameOfReference(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): TileFrameOfReference {
        val hAxisLayoutInfo = layoutInfo.xAxisInfo!!
        val vAxisLayoutInfo = layoutInfo.yAxisInfo!!

        // Set-up scales and coordinate system.
        val hScale = coordProvider.buildAxisScaleX(
            hScaleProto,
            hAxisLayoutInfo.axisDomain,
            hAxisLayoutInfo.axisLength,
            hAxisLayoutInfo.axisBreaks
        )
        val vScale = coordProvider.buildAxisScaleY(
            vScaleProto,
            vAxisLayoutInfo.axisDomain,
            vAxisLayoutInfo.axisLength,
            vAxisLayoutInfo.axisBreaks
        )
        val coord = coordProvider.createCoordinateSystem(
            hAxisLayoutInfo.axisDomain,
            hAxisLayoutInfo.axisLength,
            vAxisLayoutInfo.axisDomain,
            vAxisLayoutInfo.axisLength
        )

        val tileFrameOfReference = SquareFrameOfReference(
            hScale, vScale,
            coord,
            layoutInfo,
            theme,
            flipAxis,
        )
        tileFrameOfReference.isDebugDrawing = debugDrawing
        return tileFrameOfReference
    }


    private class AxisSpec(
        val breaksProviderFactory: AxisBreaksProviderFactory,
        val aesRange: ClosedRange<Double>,
        val label: String?,
        val theme: AxisTheme
    )
}
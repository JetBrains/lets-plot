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
    private val xScaleProto: Scale<Double>,
    private val yScaleProto: Scale<Double>,
    xAesRange: ClosedRange<Double>,
    yAesRange: ClosedRange<Double>,
    private val coordProvider: CoordProvider,
    private val theme: Theme
) : TileFrameOfReferenceProvider {

    override val flipAxis: Boolean = coordProvider.flipAxis
    private val vAxisSpec: AxisSpec
    private val hAxisSpec: AxisSpec

    init {
        val xAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(xScaleProto),
            xAesRange,
            xScaleProto.name,
            theme.axisX()
        )

        val yAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(yScaleProto),
            yAesRange,
            yScaleProto.name,
            theme.axisY()
        )

        hAxisSpec = if (flipAxis) yAxisSpec else xAxisSpec
        vAxisSpec = if (flipAxis) xAxisSpec else yAxisSpec
    }

    override val hAxisLabel: String? = if (hAxisSpec.theme.showTitle()) hAxisSpec.label else null
    override val vAxisLabel: String? = if (vAxisSpec.theme.showTitle()) vAxisSpec.label else null

    override fun createTileLayout(): TileLayout {
        val hDomain = hAxisSpec.aesRange
        val vDomain = vAxisSpec.aesRange

        val hAxisLayout = PlotAxisLayout(
            hAxisSpec.breaksProviderFactory,
            hDomain, vDomain,
            coordProvider,
            hAxisSpec.theme,
            Orientation.BOTTOM
        )

        val vAxisLayout = PlotAxisLayout(
            vAxisSpec.breaksProviderFactory,
            hDomain, vDomain,
            coordProvider,
            vAxisSpec.theme,
            Orientation.LEFT
        )

        return XYPlotTileLayout(hAxisLayout, vAxisLayout)
    }

    override fun createFrameOfReference(layoutInfo: TileLayoutInfo, debugDrawing: Boolean): TileFrameOfReference {
        val hAxisLayoutInfo = layoutInfo.xAxisInfo!!
        val vAxisLayoutInfo = layoutInfo.yAxisInfo!!

        val hScaleProto = if (flipAxis) yScaleProto else xScaleProto
        val vScaleProto = if (flipAxis) xScaleProto else yScaleProto

        // Set-up scales and coordinate system.
        val hScale = coordProvider.buildAxisScaleX(
            hScaleProto,
            hAxisLayoutInfo.axisDomain!!,
            hAxisLayoutInfo.axisLength,
            hAxisLayoutInfo.axisBreaks!!
        )
        val vScale = coordProvider.buildAxisScaleY(
            vScaleProto,
            vAxisLayoutInfo.axisDomain!!,
            vAxisLayoutInfo.axisLength,
            vAxisLayoutInfo.axisBreaks!!
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
/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.layout.tile.InsideOutTileLayout
import jetbrains.datalore.plot.builder.layout.tile.TopDownTileLayout
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.Theme

internal class SquareFrameOfReferenceProvider(
    private val hScaleProto: Scale<Double>,
    private val vScaleProto: Scale<Double>,
    hDomain: DoubleSpan,
    vDomain: DoubleSpan,
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

    override fun createTileLayoutProvider(): TileLayoutProvider {
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

        val hDomain = hAxisSpec.domainTransformed
        val vDomain = vAxisSpec.domainTransformed

        return MyTileLayoutProvider(hAxisLayout, vAxisLayout, hDomain, vDomain)
    }

    override fun createFrameOfReference(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): TileFrameOfReference {
        val hAxisLayoutInfo = layoutInfo.hAxisInfo!!
        val vAxisLayoutInfo = layoutInfo.vAxisInfo!!

        // Set-up scales and coordinate system.
        val hScaleMapper = coordProvider.buildAxisXScaleMapper(
            hAxisLayoutInfo.axisDomain,
            hAxisLayoutInfo.axisLength,
        )
        val vScaleMapper = coordProvider.buildAxisYScaleMapper(
            vAxisLayoutInfo.axisDomain,
            vAxisLayoutInfo.axisLength,
        )

        val hScale = coordProvider.buildAxisScaleX(
            hScaleProto,
            hAxisLayoutInfo.axisDomain,
            hAxisLayoutInfo.axisBreaks
        )
        val vScale = coordProvider.buildAxisScaleY(
            vScaleProto,
            vAxisLayoutInfo.axisDomain,
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
            hScaleMapper, vScaleMapper,
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
        val domainTransformed: DoubleSpan,
        val label: String?,
        val theme: AxisTheme
    )

    private class MyTileLayoutProvider(
        private val hAxisLayout: AxisLayout,
        private val vAxisLayout: AxisLayout,
        private val hDomain: DoubleSpan, // transformed data ranges.
        private val vDomain: DoubleSpan,
    ) : TileLayoutProvider {
        override fun createTopDownTileLayout(): TileLayout {
            return TopDownTileLayout(hAxisLayout, vAxisLayout, hDomain, vDomain)
        }

        override fun createInsideOutTileLayout(): TileLayout {
            return InsideOutTileLayout(hAxisLayout, vAxisLayout, hDomain, vDomain)
        }
    }
}
/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.MarginalLayerCoordProvider
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
    private val theme: Theme,
    private val marginsLayout: GeomMarginsLayout,
    private val domainByMargin: Map<MarginSide, DoubleSpan>,
) : FrameOfReferenceProvider {

    private val hAxisSpec: AxisSpec
    private val vAxisSpec: AxisSpec

    init {
        hAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(hScaleProto),
            hDomain,
            hScaleProto.name,
            theme.horizontalAxis(flipAxis)
        )

        vAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(vScaleProto),
            vDomain,
            vScaleProto.name,
            theme.verticalAxis(flipAxis)
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

        return MyTileLayoutProvider(hAxisLayout, vAxisLayout, hDomain, vDomain, marginsLayout)
    }

    override fun createTileFrame(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): FrameOfReference {
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
            marginsLayout,
            theme,
            flipAxis,
        )
        tileFrameOfReference.isDebugDrawing = debugDrawing
        return tileFrameOfReference
    }

    override fun createMarginalFrames(
        tileLayoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): Map<MarginSide, FrameOfReference> {
        check(!coordProvider.flipAxis) {
            "`flipped` corrdinate system is not supported on plots with marginal layers."
        }

        val inner = tileLayoutInfo.geomInnerBounds
        val outer = tileLayoutInfo.geomOuterBounds

        val origins = mapOf(
            MarginSide.LEFT to DoubleVector(outer.left, inner.top),
            MarginSide.TOP to DoubleVector(inner.left, outer.top),
            MarginSide.RIGHT to DoubleVector(inner.right, inner.top),
            MarginSide.BOTTOM to DoubleVector(inner.left, inner.bottom),
        )

        val sizes = mapOf(
            MarginSide.LEFT to DoubleVector(inner.left - outer.left, inner.height),
            MarginSide.TOP to DoubleVector(inner.width, inner.top - outer.top),
            MarginSide.RIGHT to DoubleVector(outer.right - inner.right, inner.height),
            MarginSide.BOTTOM to DoubleVector(inner.width, outer.bottom - inner.bottom),
        )

        val boundsByMargin = origins.mapValues { (margin, origin) ->
            DoubleRectangle(origin, sizes.getValue(margin))
        }

        val hAxisLayoutInfo = tileLayoutInfo.hAxisInfo!!
        val vAxisLayoutInfo = tileLayoutInfo.vAxisInfo!!
        return domainByMargin.mapValues { (side, domain) ->
            val xDomain = when (side) {
                MarginSide.LEFT, MarginSide.RIGHT -> domain
                MarginSide.TOP, MarginSide.BOTTOM -> hAxisLayoutInfo.axisDomain
            }
            val yDomain = when (side) {
                MarginSide.LEFT, MarginSide.RIGHT -> vAxisLayoutInfo.axisDomain
                MarginSide.TOP, MarginSide.BOTTOM -> domain
            }

            val xSize = sizes.getValue(side).x
            val ySize = sizes.getValue(side).y

            val marginCoordProvider = MarginalLayerCoordProvider(side, coordProvider)
            val marginHMapper = marginCoordProvider.buildAxisXScaleMapper(xDomain, xSize)
            val marginVMapper = marginCoordProvider.buildAxisYScaleMapper(yDomain, ySize)

            MarginalFrameOfReference(
                boundsByMargin.getValue(side),
                marginHMapper, marginVMapper,
                marginCoordProvider.createCoordinateSystem(xDomain, xSize, yDomain, ySize),
                debugDrawing,
            )
        }
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
        private val marginsLayout: GeomMarginsLayout,
    ) : TileLayoutProvider {
        override fun createTopDownTileLayout(): TileLayout {
            return TopDownTileLayout(hAxisLayout, vAxisLayout, hDomain, vDomain, marginsLayout)
        }

        override fun createInsideOutTileLayout(): TileLayout {
            return InsideOutTileLayout(hAxisLayout, vAxisLayout, hDomain, vDomain, marginsLayout)
        }
    }
}
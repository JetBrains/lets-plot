/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.frame

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Scale
import jetbrains.datalore.plot.builder.FrameOfReference
import jetbrains.datalore.plot.builder.FrameOfReferenceProvider
import jetbrains.datalore.plot.builder.MarginSide
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.MarginalLayerCoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.*
import jetbrains.datalore.plot.builder.layout.axis.AxisBreaksProviderFactory
import jetbrains.datalore.plot.builder.layout.tile.InsideOutTileLayout
import jetbrains.datalore.plot.builder.layout.tile.TopDownTileLayout
import jetbrains.datalore.plot.builder.scale.AxisPosition
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.Theme
import kotlin.math.max

internal class SquareFrameOfReferenceProvider(
    private val hScaleProto: Scale<Double>,
    private val vScaleProto: Scale<Double>,
    private val adjustedDomain: DoubleRectangle,
    override val flipAxis: Boolean,
    private val hAxisPosition: AxisPosition,
    private val vAxisPosition: AxisPosition,
    private val theme: Theme,
    private val marginsLayout: GeomMarginsLayout,
    private val domainByMargin: Map<MarginSide, DoubleSpan>,
) : FrameOfReferenceProvider {

    private val hAxisSpec: AxisSpec
    private val vAxisSpec: AxisSpec

    init {
        hAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(hScaleProto),
            hScaleProto.name,
            theme.horizontalAxis(flipAxis)
        )

        vAxisSpec = AxisSpec(
            AxisBreaksProviderFactory.forScale(vScaleProto),
            vScaleProto.name,
            theme.verticalAxis(flipAxis)
        )
    }

    override val hAxisLabel: String? = if (hAxisSpec.theme.showTitle()) hAxisSpec.label else null
    override val vAxisLabel: String? = if (vAxisSpec.theme.showTitle()) vAxisSpec.label else null

    override fun createTileLayoutProvider(): TileLayoutProvider {
        // ToDo: handle axis on both sides.
        val hAxisOrientation = when (hAxisPosition) {
            AxisPosition.TOP -> Orientation.TOP
            AxisPosition.BOTTOM -> Orientation.BOTTOM
            AxisPosition.TB -> Orientation.BOTTOM
            else -> throw IllegalStateException("Horizontal axis position: $hAxisPosition")
        }

        val vAxisOrientation = when (vAxisPosition) {
            AxisPosition.LEFT -> Orientation.LEFT
            AxisPosition.RIGHT -> Orientation.RIGHT
            AxisPosition.LR -> Orientation.LEFT
            else -> throw IllegalStateException("Vertical axis position: $vAxisPosition")
        }

        val hAxisLayout = PlotAxisLayout(
            hAxisSpec.breaksProviderFactory,
            hAxisOrientation,
            hAxisSpec.theme
        )

        val vAxisLayout = PlotAxisLayout(
            vAxisSpec.breaksProviderFactory,
            vAxisOrientation,
            vAxisSpec.theme
        )

        return MyTileLayoutProvider(hAxisLayout, vAxisLayout, adjustedDomain, marginsLayout)
    }

    override fun createTileFrame(
        layoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): FrameOfReference {
        val hAxisLayoutInfo = layoutInfo.hAxisInfo!!
        val vAxisLayoutInfo = layoutInfo.vAxisInfo!!

        // Set-up scales and coordinate system.
        val client = DoubleVector(
            hAxisLayoutInfo.axisLength,
            vAxisLayoutInfo.axisLength
        )

        val coord = coordProvider.createCoordinateSystem(adjustedDomain, client)

        val hScale = hScaleProto.with()
            .breaks(hAxisLayoutInfo.axisBreaks.domainValues)
            .labels(hAxisLayoutInfo.axisBreaks.labels)
            .build()

        val vScale = vScaleProto.with()
            .breaks(vAxisLayoutInfo.axisBreaks.domainValues)
            .labels(vAxisLayoutInfo.axisBreaks.labels)
            .build()

        val tileFrameOfReference = SquareFrameOfReference(
            hScaleBreaks = hScale.getScaleBreaks(),
            vScaleBreaks = vScale.getScaleBreaks(),
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

    override fun createMarginalFrames(
        tileLayoutInfo: TileLayoutInfo,
        coordProvider: CoordProvider,
        debugDrawing: Boolean
    ): Map<MarginSide, FrameOfReference> {
        if (domainByMargin.isEmpty()) {
            return emptyMap()
        }

        check(!coordProvider.flipped) {
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
            MarginSide.LEFT to DoubleVector(max(0.0, inner.left - outer.left), inner.height),
            MarginSide.TOP to DoubleVector(inner.width, max(0.0, inner.top - outer.top)),
            MarginSide.RIGHT to DoubleVector(max(0.0, outer.right - inner.right), inner.height),
            MarginSide.BOTTOM to DoubleVector(inner.width, max(0.0, outer.bottom - inner.bottom)),
        )

        val boundsByMargin = origins.mapValues { (margin, origin) ->
            DoubleRectangle(origin, sizes.getValue(margin))
        }

        val hAxisLayoutInfo = tileLayoutInfo.hAxisInfo!!
        val vAxisLayoutInfo = tileLayoutInfo.vAxisInfo!!
        return domainByMargin.mapValues { (side, domain) ->
            val hDomain = when (side) {
                MarginSide.LEFT, MarginSide.RIGHT -> domain
                MarginSide.TOP, MarginSide.BOTTOM -> hAxisLayoutInfo.axisDomain
            }
            val vDomain = when (side) {
                MarginSide.LEFT, MarginSide.RIGHT -> vAxisLayoutInfo.axisDomain
                MarginSide.TOP, MarginSide.BOTTOM -> domain
            }

            val marginCoordProvider = MarginalLayerCoordProvider()
            val clientSize = sizes.getValue(side)
            val adjustedDomain = DoubleRectangle(hDomain, vDomain)
            val coord = marginCoordProvider.createCoordinateSystem(
                adjustedDomain = adjustedDomain,
                clientSize = clientSize,
            )
            MarginalFrameOfReference(
                boundsByMargin.getValue(side),
                adjustedDomain = adjustedDomain,
                coord,
                debugDrawing,
            )
        }
    }


    private class AxisSpec(
        val breaksProviderFactory: AxisBreaksProviderFactory,
        val label: String?,
        val theme: AxisTheme
    )

    private class MyTileLayoutProvider(
        private val hAxisLayout: AxisLayout,
        private val vAxisLayout: AxisLayout,
        private val adjustedDomain: DoubleRectangle,
        private val marginsLayout: GeomMarginsLayout,
    ) : TileLayoutProvider {
        override fun createTopDownTileLayout(): TileLayout {
            return TopDownTileLayout(
                hAxisLayout, vAxisLayout,
                hDomain = adjustedDomain.xRange(),
                vDomain = adjustedDomain.yRange(),
                marginsLayout
            )
        }

        override fun createInsideOutTileLayout(): TileLayout {
            return InsideOutTileLayout(
                hAxisLayout, vAxisLayout,
                hDomain = adjustedDomain.xRange(),
                vDomain = adjustedDomain.yRange(),
                marginsLayout
            )
        }
    }
}
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
        fun toAxisLayout(
            orientation: Orientation,
            position: AxisPosition,
            spec: AxisSpec
        ): AxisLayout? {
            @Suppress("NAME_SHADOWING")
            val orientation: Orientation? = when (orientation) {
                Orientation.LEFT -> if (position.isLeft) orientation else null
                Orientation.RIGHT -> if (position.isRight) orientation else null
                Orientation.TOP -> if (position.isTop) orientation else null
                Orientation.BOTTOM -> if (position.isBottom) orientation else null
            }

            return orientation?.run {
                AxisLayout(
                    spec.breaksProviderFactory,
                    orientation,
                    spec.theme
                )
            }
        }

        val axisLayoutQuad = AxisLayoutQuad(
            left = toAxisLayout(Orientation.LEFT, vAxisPosition, vAxisSpec),
            right = toAxisLayout(Orientation.RIGHT, vAxisPosition, vAxisSpec),
            top = toAxisLayout(Orientation.TOP, hAxisPosition, hAxisSpec),
            bottom = toAxisLayout(Orientation.BOTTOM, hAxisPosition, hAxisSpec),
        )

        return MyTileLayoutProvider(axisLayoutQuad, adjustedDomain, marginsLayout)
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

        // Below use any of horizontal/vertical axis info in the "quad".
        // ToDo: with non-rectangular coordinates this might not work as axis length (for example) might be different
        // for top and botto, axis.
        val hAxisLayoutInfo = tileLayoutInfo.axisInfos.bottom
            ?: tileLayoutInfo.axisInfos.top
            ?: throw IllegalStateException("No top/bottom axis info.")

        val vAxisLayoutInfo = tileLayoutInfo.axisInfos.left
            ?: tileLayoutInfo.axisInfos.right
            ?: throw IllegalStateException("No left/right axis info.")

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
        private val axisLayoutQuad: AxisLayoutQuad,
        private val adjustedDomain: DoubleRectangle,
        private val marginsLayout: GeomMarginsLayout,
    ) : TileLayoutProvider {
        override fun createTopDownTileLayout(): TileLayout {
            return TopDownTileLayout(
                axisLayoutQuad,
                hDomain = adjustedDomain.xRange(),
                vDomain = adjustedDomain.yRange(),
                marginsLayout
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
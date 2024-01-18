/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.frame

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReference
import org.jetbrains.letsPlot.core.plot.builder.FrameOfReferenceProvider
import org.jetbrains.letsPlot.core.plot.builder.MarginSide
import org.jetbrains.letsPlot.core.plot.builder.coord.CoordProvider
import org.jetbrains.letsPlot.core.plot.builder.coord.MarginalLayerCoordProvider
import org.jetbrains.letsPlot.core.plot.builder.guide.Orientation
import org.jetbrains.letsPlot.core.plot.builder.layout.*
import org.jetbrains.letsPlot.core.plot.builder.layout.axis.AxisBreaksProviderFactory
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.InsideOutTileLayout
import org.jetbrains.letsPlot.core.plot.builder.layout.tile.PolarTileLayout
import org.jetbrains.letsPlot.core.plot.builder.scale.AxisPosition
import kotlin.math.max

internal class PolarFrameOfReferenceProvider(
    private val hScaleProto: Scale,
    private val vScaleProto: Scale,
    private val dataDomain: DoubleRectangle,
    private val plotDomain: DoubleRectangle,
    override val flipAxis: Boolean,
    private val theme: Theme,
    private val marginsLayout: GeomMarginsLayout,
    private val domainByMargin: Map<MarginSide, DoubleSpan>,
) : FrameOfReferenceProvider {

    private val hAxisPosition: AxisPosition = AxisPosition.BOTTOM
    private val vAxisPosition: AxisPosition = AxisPosition.LEFT

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

        return MyTileLayoutProvider(axisLayoutQuad, plotDomain, marginsLayout)
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

        val coord = coordProvider.createCoordinateSystem(plotDomain, client)

        val hScale = hScaleProto.with()
            .breaks(hAxisLayoutInfo.axisBreaks.domainValues)
            .labels(hAxisLayoutInfo.axisBreaks.labels)
            .build()

        val vScale = vScaleProto.with()
            .breaks(vAxisLayoutInfo.axisBreaks.domainValues)
            .labels(vAxisLayoutInfo.axisBreaks.labels)
            .build()

        val tileFrameOfReference = PolarFrameOfReference(
            hScaleBreaks = hScale.getScaleBreaks(),
            vScaleBreaks = vScale.getScaleBreaks(),
            dataDomain,
            plotDomain,
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
        plotBackground: Color,
        penColor: Color,
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
                plotBackground,
                penColor,
                debugDrawing,
                theme.exponentFormat.superscript
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
            return PolarTileLayout(
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
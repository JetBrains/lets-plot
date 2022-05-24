/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.FeatureSwitch
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.guide.Orientation
import jetbrains.datalore.plot.builder.layout.AxisLayout
import jetbrains.datalore.plot.builder.layout.AxisLayoutInfo
import jetbrains.datalore.plot.builder.layout.TileLayout
import jetbrains.datalore.plot.builder.layout.TileLayoutInfo

internal class InsideOutTileLayout constructor(
    private val hAxisLayout: AxisLayout,
    private val vAxisLayout: AxisLayout,
    private val hDomain: DoubleSpan, // transformed data ranges.
    private val vDomain: DoubleSpan,
) : TileLayout {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun doLayout(geomSize: DoubleVector, coordProvider: CoordProvider): TileLayoutInfo {

        val geomOuterBounds = DoubleRectangle(DoubleVector.ZERO, geomSize)
        val geomInnerBounds = geomOuterBounds.let {
            when {
                FeatureSwitch.MARGINAL_LAYERS -> FeatureSwitch.subtactMarginalLayers(it)
                else -> it
            }
        }

        var (hAxisInfo, vAxisInfo) = computeAxisInfos(
            hAxisLayout,
            vAxisLayout,
            geomSize = geomInnerBounds.dimension,
            hDomain, vDomain,
        )

        // Combine geom area and x/y-axis
        val geomWithAxisBounds = tileBounds(
            hAxisInfo.axisBounds(),
            vAxisInfo.axisBounds(),
            geomOuterBounds
        )


        return TileLayoutInfo(
            offset = DoubleVector.ZERO,
            bounds = geomWithAxisBounds,
            geomOuterBounds = geomOuterBounds,
            geomInnerBounds = geomInnerBounds,
            clipBounds = TileLayoutUtil.clipBounds(geomInnerBounds),
            hAxisInfo,
            vAxisInfo,
            hAxisShown = true,
            vAxisShown = true,
            trueIndex = 0
        )
    }

    companion object {
        private const val AXIS_STRETCH_RATIO = 0.1  // allow 10% axis flexibility (on each end)

        private fun tileBounds(
            xAxisBounds: DoubleRectangle,
            yAxisBounds: DoubleRectangle,
            geomBounds: DoubleRectangle
        ): DoubleRectangle {
            // Can't just union bounds because
            // x-axis has zero origin
            // y-axis has negative origin
            val leftTop = DoubleVector(
                geomBounds.left - yAxisBounds.width,
                geomBounds.top - TileLayoutUtil.GEOM_MARGIN
            )
            val rightBottom = DoubleVector(
                geomBounds.right + TileLayoutUtil.GEOM_MARGIN,
                geomBounds.bottom + xAxisBounds.height
            )
            return DoubleRectangle(leftTop, rightBottom.subtract(leftTop))
        }

        private fun computeAxisInfos(
            hAxisLayout: AxisLayout,
            vAxisLayout: AxisLayout,
            geomSize: DoubleVector,
            hDomain: DoubleSpan,
            vDomain: DoubleSpan,
        ): Pair<AxisLayoutInfo, AxisLayoutInfo> {
            val geomBounds = DoubleRectangle(DoubleVector.ZERO, geomSize)
            var hAxisInfo = computeHAxisInfo(
                hAxisLayout,
                hDomain,
                geomBounds,
            )

            var vAxisInfo = computeVAxisInfo(
                vAxisLayout,
                vDomain,
                geomBounds
            )

            return Pair(hAxisInfo, vAxisInfo)
        }

        private fun computeHAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            geomBounds: DoubleRectangle
        ): AxisLayoutInfo {
            val axisSpan = geomBounds.xRange()
            val axisLength = axisSpan.length
            val stretch = axisLength * AXIS_STRETCH_RATIO
            val maxTickLabelsBounds = TileLayoutUtil.maxHAxisTickLabelsBounds(
                Orientation.BOTTOM,
                stretch,
                axisSpan = axisSpan,
                maxHorizontalSpan = axisSpan
            )
            return axisLayout.doLayout(axisDomain, axisLength, maxTickLabelsBounds)
        }

        private fun computeVAxisInfo(
            axisLayout: AxisLayout,
            axisDomain: DoubleSpan,
            geomBounds: DoubleRectangle
        ): AxisLayoutInfo {
            return axisLayout.doLayout(axisDomain, geomBounds.dimension.y, null)
        }
    }
}
package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.theme.LegendTheme
import kotlin.math.max

class LegendBoxesLayout(private val myPlotBounds: DoubleRectangle, private val myTheme: LegendTheme) {

    fun doLayout(infos: List<LegendBoxInfo>): Result {
        val legendPosition = myTheme.position()
        val legendJustification = myTheme.justification()

        // ToDo: theme legend.box option
        val legendArrangement = jetbrains.datalore.plot.builder.guide.LegendArrangement.VERTICAL

        val plotCenter = myPlotBounds.center
        var plotInnerBoundsWithoutLegendBoxes = myPlotBounds

        val boxWithLocationList = if (legendArrangement === jetbrains.datalore.plot.builder.guide.LegendArrangement.VERTICAL)
            LegendBoxesLayoutUtil.verticalStack(infos)
        else
            LegendBoxesLayoutUtil.horizontalStack(infos)

        val boxesSize = LegendBoxesLayoutUtil.size(boxWithLocationList)

        // adjust plot bounds
        if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.LEFT || legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.RIGHT) {
            val plotWidth = max(0.0, plotInnerBoundsWithoutLegendBoxes.width - boxesSize.x)
            if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.LEFT) {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeWidthKeepRight(plotInnerBoundsWithoutLegendBoxes, plotWidth)
            } else {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeWidth(plotInnerBoundsWithoutLegendBoxes, plotWidth)
            }
        } else if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.TOP || legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.BOTTOM) {
            val plotHeight = max(0.0, plotInnerBoundsWithoutLegendBoxes.height - boxesSize.y)
            if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.TOP) {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeHeightKeepBottom(plotInnerBoundsWithoutLegendBoxes, plotHeight)
            } else {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeHeight(plotInnerBoundsWithoutLegendBoxes, plotHeight)
            }
        }

        val legendOrigin: DoubleVector
        if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.LEFT) {
            legendOrigin = DoubleVector(
                    plotInnerBoundsWithoutLegendBoxes.left - boxesSize.x,
                    plotCenter.y - boxesSize.y / 2)
        } else if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.RIGHT) {
            legendOrigin = DoubleVector(
                    plotInnerBoundsWithoutLegendBoxes.right,
                    plotCenter.y - boxesSize.y / 2)
        } else if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.TOP) {
            legendOrigin = DoubleVector(
                    plotCenter.x - boxesSize.x / 2,
                    plotInnerBoundsWithoutLegendBoxes.top - boxesSize.y)
        } else if (legendPosition == jetbrains.datalore.plot.builder.guide.LegendPosition.BOTTOM) {
            legendOrigin = DoubleVector(
                    plotCenter.x - boxesSize.x / 2,
                    plotInnerBoundsWithoutLegendBoxes.bottom)
        } else {
            legendOrigin = LegendBoxesLayoutUtil.overlayLegendOrigin(plotInnerBoundsWithoutLegendBoxes, boxesSize, legendPosition, legendJustification)
        }

        val resultBoxWithLocationList = LegendBoxesLayoutUtil.moveAll(legendOrigin, boxWithLocationList)
        return Result(plotInnerBoundsWithoutLegendBoxes, resultBoxWithLocationList)
    }

    class Result(val plotInnerBoundsWithoutLegendBoxes: DoubleRectangle, locations: List<BoxWithLocation>) {
        val boxWithLocationList: List<BoxWithLocation> = ArrayList(locations)
    }

    class BoxWithLocation internal constructor(val legendBox: LegendBoxInfo, val location: DoubleVector) {

        internal fun size(): DoubleVector {
            return legendBox.size
        }

        internal fun bounds(): DoubleRectangle {
            return DoubleRectangle(location, legendBox.size)
        }
    }
}

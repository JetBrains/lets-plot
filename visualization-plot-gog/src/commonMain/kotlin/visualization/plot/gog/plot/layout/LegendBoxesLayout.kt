package jetbrains.datalore.visualization.plot.gog.plot.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendArrangement
import jetbrains.datalore.visualization.plot.gog.plot.guide.LegendPosition
import jetbrains.datalore.visualization.plot.gog.plot.theme.LegendTheme
import kotlin.math.max

class LegendBoxesLayout(private val myPlotBounds: DoubleRectangle, private val myTheme: LegendTheme) {

    fun doLayout(infos: List<LegendBoxInfo>): Result {
        val legendPosition = myTheme.position()
        val legendJustification = myTheme.justification()

        // ToDo: theme legend.box option
        val legendArrangement = LegendArrangement.VERTICAL

        val plotCenter = myPlotBounds.center
        var plotInnerBoundsWithoutLegendBoxes = myPlotBounds

        val boxWithLocationList = if (legendArrangement === LegendArrangement.VERTICAL)
            LegendBoxesLayoutUtil.verticalStack(infos)
        else
            LegendBoxesLayoutUtil.horizontalStack(infos)

        val boxesSize = LegendBoxesLayoutUtil.size(boxWithLocationList)

        // adjust plot bounds
        if (legendPosition == LegendPosition.LEFT || legendPosition == LegendPosition.RIGHT) {
            val plotWidth = max(0.0, plotInnerBoundsWithoutLegendBoxes.width - boxesSize.x)
            if (legendPosition == LegendPosition.LEFT) {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeWidthKeepRight(plotInnerBoundsWithoutLegendBoxes, plotWidth)
            } else {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeWidth(plotInnerBoundsWithoutLegendBoxes, plotWidth)
            }
        } else if (legendPosition == LegendPosition.TOP || legendPosition == LegendPosition.BOTTOM) {
            val plotHeight = max(0.0, plotInnerBoundsWithoutLegendBoxes.height - boxesSize.y)
            if (legendPosition == LegendPosition.TOP) {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeHeightKeepBottom(plotInnerBoundsWithoutLegendBoxes, plotHeight)
            } else {
                plotInnerBoundsWithoutLegendBoxes = GeometryUtil.changeHeight(plotInnerBoundsWithoutLegendBoxes, plotHeight)
            }
        }

        val legendOrigin: DoubleVector
        if (legendPosition == LegendPosition.LEFT) {
            legendOrigin = DoubleVector(
                    plotInnerBoundsWithoutLegendBoxes.left - boxesSize.x,
                    plotCenter.y - boxesSize.y / 2)
        } else if (legendPosition == LegendPosition.RIGHT) {
            legendOrigin = DoubleVector(
                    plotInnerBoundsWithoutLegendBoxes.right,
                    plotCenter.y - boxesSize.y / 2)
        } else if (legendPosition == LegendPosition.TOP) {
            legendOrigin = DoubleVector(
                    plotCenter.x - boxesSize.x / 2,
                    plotInnerBoundsWithoutLegendBoxes.top - boxesSize.y)
        } else if (legendPosition == LegendPosition.BOTTOM) {
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

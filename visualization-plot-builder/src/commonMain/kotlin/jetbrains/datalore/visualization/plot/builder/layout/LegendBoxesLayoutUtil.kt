package jetbrains.datalore.visualization.plot.builder.layout

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.builder.guide.LegendJustification
import jetbrains.datalore.visualization.plot.builder.guide.LegendPosition

internal object LegendBoxesLayoutUtil {
    fun verticalStack(boxInfos: List<LegendBoxInfo>): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        var y = 0.0
        for (info in boxInfos) {
            result.add(LegendBoxesLayout.BoxWithLocation(info, DoubleVector(0.0, y)))
            y += info.size.y
        }
        return result
    }

    fun horizontalStack(boxInfos: List<LegendBoxInfo>): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        var x = 0.0
        for (info in boxInfos) {
            result.add(LegendBoxesLayout.BoxWithLocation(info, DoubleVector(x, 0.0)))
            x += info.size.x
        }
        return result
    }

    fun moveAll(delta: DoubleVector, boxWithLocationList: List<LegendBoxesLayout.BoxWithLocation>): List<LegendBoxesLayout.BoxWithLocation> {
        val result = ArrayList<LegendBoxesLayout.BoxWithLocation>()
        for (boxWithLocation in boxWithLocationList) {
            result.add(LegendBoxesLayout.BoxWithLocation(boxWithLocation.legendBox, boxWithLocation.location.add(delta)))
        }
        return result
    }

    fun size(boxWithLocationList: List<LegendBoxesLayout.BoxWithLocation>): DoubleVector {
        var bounds: DoubleRectangle? = null
        for (boxWithLocation in boxWithLocationList) {
            bounds = bounds?.union(boxWithLocation.bounds()) ?: boxWithLocation.bounds()
        }

        return bounds?.dimension ?: DoubleVector.ZERO
    }

    fun overlayLegendOrigin(
            plotBounds: DoubleRectangle, legendSize: DoubleVector, legendPosition: LegendPosition, legendJustification: LegendJustification): DoubleVector {
        val plotSize = plotBounds.dimension

        // [0,0] -> bottom-left, [1,1] -> top, right
        val absolutePosition = DoubleVector(
                plotBounds.left + plotSize.x * legendPosition.x,
                plotBounds.bottom - plotSize.y * legendPosition.y
        )

        // legendJustification: [0,0] -> bottom-left, [1,1] -> top, right
        val originOffset = DoubleVector(
                -legendSize.x * legendJustification.x,
                legendSize.y * legendJustification.y - legendSize.y
        )

        return absolutePosition.add(originOffset)
    }
}

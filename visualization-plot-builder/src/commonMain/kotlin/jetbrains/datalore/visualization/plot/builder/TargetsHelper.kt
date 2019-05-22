package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LocatedTargets
import jetbrains.datalore.visualization.plot.builder.event3.TooltipSpecFactory
import jetbrains.datalore.visualization.plot.builder.event3.TransformedTargetLocator
import jetbrains.datalore.visualization.plot.builder.event3.tooltip.TooltipSpec

internal class TargetsHelper {
    private val myTileInfoList = ArrayList<TileInfo>()

    fun getTargetTooltipSpec(plotCoord: DoubleVector): List<TooltipSpec> {
        val tileInfo = findTileInfo(plotCoord) ?: return emptyList()

        val locatedTargetsList = tileInfo.findTargets(plotCoord)
        return createTargetTooltipSpecs(locatedTargetsList, tileInfo.axisOrigin)
    }

    private fun findTileInfo(plotCoord: DoubleVector): TileInfo? {
        for (tileInfo in myTileInfoList) {
            if (tileInfo.contains(plotCoord)) {
                return tileInfo
            }
        }

        return null
    }

    private fun createTargetTooltipSpecs(locatedTargetsList: List<LocatedTargets>, axisOrigin: DoubleVector): List<TooltipSpec> {
        val tooltipSpecs = ArrayList<TooltipSpec>()

        locatedTargetsList.forEach { locatedTarget ->
            val factory = TooltipSpecFactory(locatedTarget.contextualMapping, axisOrigin)
            locatedTarget.geomTargets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget)) }
        }

        return tooltipSpecs
    }

    fun addTileTargetLocators(tileRect: DoubleRectangle, targetLocators: List<GeomTargetLocator>) {
        val tileInfo = TileInfo(tileRect)
        targetLocators.forEach { tileInfo.addTargetLocator(it) }
        myTileInfoList.add(tileInfo)
    }

    private class TileInfo internal constructor(private val myTilePlotRect: DoubleRectangle) {
        private val myGeomTargetLocators = ArrayList<GeomTargetLocator>()

        internal val axisOrigin: DoubleVector
            get() = DoubleVector(myTilePlotRect.left, myTilePlotRect.bottom)

        internal fun addTargetLocator(geomTargetLocator: GeomTargetLocator) {
            myGeomTargetLocators.add(TileTargetLocator(geomTargetLocator))
        }

        internal fun findTargets(plotCoord: DoubleVector): List<LocatedTargets> {
            val targetsSolver = TargetsSolver()
            myGeomTargetLocators.forEach { locator -> targetsSolver.addLocatedTargets(locator.findTargets(plotCoord)) }
            return targetsSolver.solve()
        }

        internal operator fun contains(plotCoord: DoubleVector): Boolean {
            return myTilePlotRect.contains(plotCoord)
        }

        private inner class TileTargetLocator(locator: GeomTargetLocator) : TransformedTargetLocator(locator) {

            override fun convertToTargetCoord(coord: DoubleVector): DoubleVector {
                return coord.subtract(myTilePlotRect.origin)
            }

            override fun convertToPlotCoord(coord: DoubleVector): DoubleVector {
                return coord.add(myTilePlotRect.origin)
            }

            override fun convertToPlotDistance(distance: Double): Double {
                return distance
            }
        }
    }
}

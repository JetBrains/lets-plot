package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LocatedTargets
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpec
import jetbrains.datalore.visualization.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.visualization.plot.builder.interact.TransformedTargetLocator

internal class PlotTooltipHelper {
    private val myTileInfos = ArrayList<TileInfo>()

    fun addTileInfo(geomBounds: DoubleRectangle, targetLocators: List<GeomTargetLocator>) {
        val tileInfo = TileInfo(geomBounds, targetLocators)
        myTileInfos.add(tileInfo)
    }

    fun createTooltipSpecs(plotCoord: DoubleVector): List<TooltipSpec> {
        val tileInfo = findTileInfo(plotCoord) ?: return emptyList()

        val locatedTargetsList = tileInfo.findTargets(plotCoord)
        return createTooltipSpecs(locatedTargetsList, tileInfo.axisOrigin)
    }

    private fun findTileInfo(plotCoord: DoubleVector): TileInfo? {
        for (tileInfo in myTileInfos) {
            if (tileInfo.contains(plotCoord)) {
                return tileInfo
            }
        }

        return null
    }

    private fun createTooltipSpecs(locatedTargetsList: List<LocatedTargets>, axisOrigin: DoubleVector): List<TooltipSpec> {
        val tooltipSpecs = ArrayList<TooltipSpec>()

        locatedTargetsList.forEach { locatedTarget ->
            val factory = TooltipSpecFactory(locatedTarget.contextualMapping, axisOrigin)
            locatedTarget.geomTargets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget)) }
        }

        return tooltipSpecs
    }

    private class TileInfo(private val geomBounds: DoubleRectangle, targetLocators: List<GeomTargetLocator>) {
        private val myTargetLocators = targetLocators.map {
            TileTargetLocator(it)
        }

        internal val axisOrigin: DoubleVector
            get() = DoubleVector(geomBounds.left, geomBounds.bottom)

        internal fun findTargets(plotCoord: DoubleVector): List<LocatedTargets> {
            val targetsSolver = LocatedTargetsPicker().apply {
                for (locator in myTargetLocators) {
                    addLocatedTargets(locator.findTargets(plotCoord))
                }
            }
            return targetsSolver.solve()
        }

        internal operator fun contains(plotCoord: DoubleVector): Boolean {
            return geomBounds.contains(plotCoord)
        }

        private inner class TileTargetLocator(locator: GeomTargetLocator) : TransformedTargetLocator(locator) {

            override fun convertToTargetCoord(coord: DoubleVector): DoubleVector {
                return coord.subtract(geomBounds.origin)
            }

            override fun convertToPlotCoord(coord: DoubleVector): DoubleVector {
                return coord.add(geomBounds.origin)
            }

            override fun convertToPlotDistance(distance: Double): Double {
                return distance
            }
        }
    }


}

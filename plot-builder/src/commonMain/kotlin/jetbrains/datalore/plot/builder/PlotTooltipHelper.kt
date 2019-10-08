package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.interact.loc.TransformedTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LookupResult

internal class PlotTooltipHelper {
    private val myTileInfos = ArrayList<jetbrains.datalore.plot.builder.PlotTooltipHelper.TileInfo>()

    fun removeAllTileInfos() {
        myTileInfos.clear()
    }

    fun addTileInfo(geomBounds: DoubleRectangle, targetLocators: List<GeomTargetLocator>) {
        val tileInfo = jetbrains.datalore.plot.builder.PlotTooltipHelper.TileInfo(geomBounds, targetLocators)
        myTileInfos.add(tileInfo)
    }

    fun createTooltipSpecs(plotCoord: DoubleVector): List<jetbrains.datalore.plot.builder.interact.TooltipSpec> {
        val tileInfo = findTileInfo(plotCoord) ?: return emptyList()

        val lookupResults = tileInfo.findTargets(plotCoord)
        return createTooltipSpecs(lookupResults, tileInfo.axisOrigin)
    }

    private fun findTileInfo(plotCoord: DoubleVector): jetbrains.datalore.plot.builder.PlotTooltipHelper.TileInfo? {
        for (tileInfo in myTileInfos) {
            if (tileInfo.contains(plotCoord)) {
                return tileInfo
            }
        }

        return null
    }

    private fun createTooltipSpecs(lookupResults: List<LookupResult>, axisOrigin: DoubleVector): List<jetbrains.datalore.plot.builder.interact.TooltipSpec> {
        val tooltipSpecs = ArrayList<jetbrains.datalore.plot.builder.interact.TooltipSpec>()

        lookupResults.forEach { result ->
            val factory =
                jetbrains.datalore.plot.builder.interact.TooltipSpecFactory(result.contextualMapping, axisOrigin)
            result.targets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget)) }
        }

        return tooltipSpecs
    }


    private class TileInfo(private val geomBounds: DoubleRectangle,
                           targetLocators: List<GeomTargetLocator>) {

        private val myTargetLocators = targetLocators.map { TileTargetLocator(it) }

        internal val axisOrigin: DoubleVector
            get() = DoubleVector(geomBounds.left, geomBounds.bottom)

        internal fun findTargets(plotCoord: DoubleVector): List<LookupResult> {
            val targetsPicker = jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker().apply {
                for (locator in myTargetLocators) {
                    val result = locator.search(plotCoord)
                    if (result != null) {
                        addLookupResult(result)
                    }
                }
            }
            return targetsPicker.picked
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

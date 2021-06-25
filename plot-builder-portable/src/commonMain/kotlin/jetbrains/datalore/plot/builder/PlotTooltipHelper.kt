/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.GeomTargetLocator.LookupResult
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker
import jetbrains.datalore.plot.builder.interact.loc.TransformedTargetLocator

internal class PlotTooltipHelper {
    private val myTileInfos = ArrayList<TileInfo>()

    fun removeAllTileInfos() {
        myTileInfos.clear()
    }

    fun addTileInfo(
        geomBounds: DoubleRectangle,
        targetLocators: List<GeomTargetLocator>,
        geomClipBounds: DoubleRectangle?,
    ) {
        val tileInfo = TileInfo(
            geomBounds,
            geomClipBounds,
            targetLocators
        )
        myTileInfos.add(tileInfo)
    }

    fun createTooltipSpecs(plotCoord: DoubleVector): List<TooltipSpec> {
        val tileInfo = findTileInfo(plotCoord) ?: return emptyList()

        val lookupResults = tileInfo.findTargets(plotCoord)
        return createTooltipSpecs(lookupResults, tileInfo.axisOrigin)
    }

    fun getGeomBounds(plotCoord: DoubleVector): DoubleRectangle? {
        val tileInfo = findTileInfo(plotCoord) ?: return null
        return tileInfo.geomBounds
    }

    fun getGeomClipBounds(plotCoord: DoubleVector): DoubleRectangle? {
        val tileInfo = findTileInfo(plotCoord) ?: return null
        return tileInfo.geomClipBounds
    }

    private fun findTileInfo(plotCoord: DoubleVector): TileInfo? {
        for (tileInfo in myTileInfos) {
            if (tileInfo.contains(plotCoord)) {
                return tileInfo
            }
        }

        return null
    }

    private fun createTooltipSpecs(lookupResults: List<LookupResult>, axisOrigin: DoubleVector): List<TooltipSpec> {
        val tooltipSpecs = ArrayList<TooltipSpec>()

        lookupResults.forEach { result ->
            val factory = TooltipSpecFactory(result.contextualMapping, axisOrigin)
            result.targets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget)) }
        }

        return tooltipSpecs
    }


    private class TileInfo(
        val geomBounds: DoubleRectangle,
        val geomClipBounds: DoubleRectangle?,
        targetLocators: List<GeomTargetLocator>
    ) {

        private val myTargetLocators = targetLocators.map { TileTargetLocator(it) }

        internal val axisOrigin: DoubleVector
            get() = DoubleVector(geomBounds.left, geomBounds.bottom)

        internal fun findTargets(plotCoord: DoubleVector): List<LookupResult> {
            val targetsPicker = LocatedTargetsPicker().apply {
                for (locator in myTargetLocators) {
                    val result = locator.search(plotCoord)
                    if (result != null) {
                        addLookupResult(result, plotCoord)
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

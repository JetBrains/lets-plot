/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayersConfigurator
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.SortingMode
import jetbrains.livemap.api.LayersBuilder

internal class LiveMapDataPointAestheticsProcessor(
    private val myAesthetics: Aesthetics,
    liveMapOptions: LiveMapOptions
) {
    private val myLayerKind: MapLayerKind = getLayerKind(liveMapOptions.displayMode)
    private val myGeodesic: Boolean = liveMapOptions.geodesic
    private val myFrameSpecified: Boolean = allAesMatch(myAesthetics, ::isFrameSet)

    val mapEntityBuilders: List<MapEntityBuilder>
        get() = (if (useMultiDataPoint()) processMultiDataPoints() else processDataPoints()).onEach { it.layerIndex = 0 }

    private fun isFrameSet(p: DataPointAesthetics): Boolean {
        return p.frame() != AesInitValue[Aes.FRAME]
    }

    private fun getLayerKind(displayMode: LivemapConstants.DisplayMode): MapLayerKind {
        return when (displayMode) {
            LivemapConstants.DisplayMode.POINT -> MapLayerKind.POINT
            LivemapConstants.DisplayMode.PIE -> MapLayerKind.PIE
            LivemapConstants.DisplayMode.BAR -> MapLayerKind.BAR
        }
    }

    private fun getSortingMode(layerKind: MapLayerKind): SortingMode = when(layerKind) {
        MapLayerKind.PIE -> SortingMode.PIE_CHART
        MapLayerKind.BAR -> SortingMode.BAR
        else -> error("Wrong layer kind: $layerKind")
    }

    fun heatMapWithFrame() = myLayerKind == MapLayerKind.HEATMAP && myFrameSpecified

    fun createConfigurator(): LayersBuilder.() -> Unit {
        return createLayersConfigurator(myLayerKind, mapEntityBuilders)
    }

    private fun processDataPoints(): List<MapEntityBuilder> {
        return myAesthetics.dataPoints()
            .map { MapEntityBuilder(it, myLayerKind).apply { setIfNeeded(it) } }
    }

    private fun processMultiDataPoints(): List<MapEntityBuilder> {
        return MultiDataPointHelper
            .getPoints(myAesthetics, getSortingMode(myLayerKind))
            .map { MapEntityBuilder(it, myLayerKind).apply { setIfNeeded(it.aes) } }
    }

    private fun useMultiDataPoint(): Boolean {
        return myLayerKind === MapLayerKind.PIE || myLayerKind === MapLayerKind.BAR
    }

    private fun MapEntityBuilder.setIfNeeded(p: DataPointAesthetics) {
        setGeometryPointIfNeeded(p, this)
        setGeodesicIfNeeded(this)
    }

    private fun setGeometryPointIfNeeded(p: DataPointAesthetics, mapEntityBuilder: MapEntityBuilder) {
        mapEntityBuilder.setGeometryPoint(explicitVec(p.x()!!, p.y()!!))
    }

    private fun setGeodesicIfNeeded(mapEntityBuilder: MapEntityBuilder) {
        if (myLayerKind == MapLayerKind.PATH) {
            mapEntityBuilder.geodesic = myGeodesic
        }
    }

    private fun allAesMatch(aes: Aesthetics, matcher: (DataPointAesthetics) -> Boolean): Boolean {
        if (aes.dataPointCount() == 0) {
            return false
        }

        for (p in aes.dataPoints()) {
            if (!matcher(p)) {
                return false
            }
        }
        return true
    }
}
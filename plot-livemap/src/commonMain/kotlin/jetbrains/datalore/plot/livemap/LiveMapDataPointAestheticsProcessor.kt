/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayersConfigurator
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.SortingMode
import jetbrains.livemap.api.LayersBuilder

internal class LiveMapDataPointAestheticsProcessor(
    private val myAesthetics: Aesthetics,
    liveMapOptions: LiveMapOptions
) {
    private val myLayerKind: MapLayerKind
    private val myGeodesic: Boolean
    private val dataPointLiveMapAesthetics: List<DataPointLiveMapAesthetics>

    init {
        myLayerKind = when (liveMapOptions.displayMode) {
            LivemapConstants.DisplayMode.POINT -> MapLayerKind.POINT
            LivemapConstants.DisplayMode.PIE -> MapLayerKind.PIE
            LivemapConstants.DisplayMode.BAR -> MapLayerKind.BAR
        }
        myGeodesic = when (myLayerKind) {
            MapLayerKind.PATH -> liveMapOptions.geodesic
            else -> false
        }

        val sortingMode = when (myLayerKind) {
            MapLayerKind.PIE -> SortingMode.PIE_CHART
            MapLayerKind.BAR -> SortingMode.BAR
            else -> null
        }

        dataPointLiveMapAesthetics = when (myLayerKind) {
            MapLayerKind.PIE, MapLayerKind.BAR -> {
                MultiDataPointHelper
                    .getPoints(myAesthetics, sortingMode!!)
                    .map { DataPointLiveMapAesthetics(it, myLayerKind).apply { setGeoAes(it.aes) } }
            }
            else -> myAesthetics.dataPoints()
                .map { DataPointLiveMapAesthetics(it, myLayerKind).apply { setGeoAes(it) } }
        }.onEach { it.layerIndex = 0 }
    }

    fun createConfigurator(): LayersBuilder.() -> Unit {
        return createLayersConfigurator(myLayerKind, dataPointLiveMapAesthetics)
    }

    private fun DataPointLiveMapAesthetics.setGeoAes(p: DataPointAesthetics) {
        this.setGeometryPoint(explicitVec(p.x()!!, p.y()!!))
        this.geodesic = myGeodesic
    }
}

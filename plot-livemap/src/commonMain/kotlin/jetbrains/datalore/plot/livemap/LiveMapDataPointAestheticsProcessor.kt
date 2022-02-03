/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayerBuilder
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.SortingMode
import jetbrains.livemap.api.LayersBuilder

internal class LiveMapDataPointAestheticsProcessor(
    myAesthetics: Aesthetics,
    private val myMappedAes: Set<Aes<*>>,
    displayMode: LivemapConstants.DisplayMode
) {
    private val myLayerKind: MapLayerKind
    private val dataPointLiveMapAesthetics: List<DataPointLiveMapAesthetics>

    init {
        myLayerKind = when (displayMode) {
            LivemapConstants.DisplayMode.POINT -> MapLayerKind.POINT
            LivemapConstants.DisplayMode.PIE -> MapLayerKind.PIE
            LivemapConstants.DisplayMode.BAR -> MapLayerKind.BAR
        }

        val sortingMode = when (myLayerKind) {
            MapLayerKind.PIE -> SortingMode.PIE_CHART
            MapLayerKind.BAR -> SortingMode.BAR
            else -> null
        }

        dataPointLiveMapAesthetics = when (myLayerKind) {
            MapLayerKind.PIE, MapLayerKind.BAR ->
                MultiDataPointHelper.getPoints(myAesthetics, sortingMode!!)
                    .map { DataPointLiveMapAesthetics(it, 0, myLayerKind).setGeometryPoint(explicitVec(it.aes.x()!!, it.aes.y()!!)) }
            else ->
                myAesthetics
                    .dataPoints()
                    .map { DataPointLiveMapAesthetics(it, 0, myLayerKind).setGeometryPoint(explicitVec(it.x()!!, it.y()!!)) }
        }
    }

    fun createConfigurator(): LayersBuilder.() -> Unit {
        return createLayerBuilder(myLayerKind, dataPointLiveMapAesthetics, myMappedAes)
    }

}

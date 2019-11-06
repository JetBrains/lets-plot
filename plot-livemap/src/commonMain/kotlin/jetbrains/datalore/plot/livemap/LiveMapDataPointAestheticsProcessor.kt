/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesInitValue
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.livemap.LiveMapUtil.createLayersBuilderBlock
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.MultiDataPoint
import jetbrains.datalore.plot.livemap.MultiDataPointHelper.SortingMode
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.mapobjects.MapLayerKind
import jetbrains.livemap.mapobjects.MapLayerKind.POINT
import jetbrains.livemap.mapobjects.MapObject
import kotlin.math.abs
import kotlin.math.max

internal class LiveMapDataPointAestheticsProcessor(
    private val myAesthetics: Aesthetics,
    liveMapOptions: LiveMapOptions
) {
    private val myStrokeWidth: Double? = liveMapOptions.stroke
    private val myLayerKind: MapLayerKind = getLayerKind(liveMapOptions.displayMode)
    private val myGeodesic: Boolean = liveMapOptions.geodesic
    private val myFrameSpecified: Boolean = allAesMatch(myAesthetics, ::isFrameSet)
    private val myLonLatInsideMapIdSpecified: Boolean = allAesMatch(myAesthetics, ::isLiveMapWithLonLat)
    private var myMaxAbsValue: Double? = null

    private val mapObjects: List<MapObject>
        get() = if (useMultiDataPoint()) processMultiDataPoints() else processDataPoints()

    private val mapObjectBuilders: List<MapObjectBuilder>
        get() = if (useMultiDataPoint()) processMultiDataPoints2() else processDataPoints2()

    private fun isFrameSet(p: DataPointAesthetics): Boolean {
        return p.frame() != AesInitValue[Aes.FRAME]
    }

    private fun isLiveMapWithLonLat(p: DataPointAesthetics): Boolean {
        return LonLatParser.parse(p.mapId().toString()) != null
    }

    private fun getLayerKind(displayMode: LivemapConstants.DisplayMode): MapLayerKind {
        return when (displayMode) {
            LivemapConstants.DisplayMode.POLYGON -> MapLayerKind.POLYGON
            LivemapConstants.DisplayMode.POINT -> POINT
            LivemapConstants.DisplayMode.PIE -> MapLayerKind.PIE
            LivemapConstants.DisplayMode.HEATMAP -> MapLayerKind.HEATMAP
            LivemapConstants.DisplayMode.BAR -> MapLayerKind.BAR
            else -> throw IllegalArgumentException("Unknown display mode: $displayMode")
        }
    }

    private fun getSortingMode(layerKind: MapLayerKind): SortingMode {
        return if (layerKind === MapLayerKind.PIE)
            SortingMode.PIE_CHART
        else
            SortingMode.BAR
    }

    private fun getMaxAbsValue(multiDataPoints: List<MultiDataPoint>): Double {
        var maxAbsValue = 0.0
        for (multiDataPoint in multiDataPoints) {
            for (value in multiDataPoint.values) {
                maxAbsValue = max(abs(value), maxAbsValue)
            }
        }
        return maxAbsValue
    }

    fun heatMapWithFrame(): Boolean {
        return myLayerKind === MapLayerKind.HEATMAP && myFrameSpecified
    }

    fun createMapLayer(): MapLayer {
        return MapLayer(myLayerKind, mapObjects)
    }

    fun createBlock(): LayersBuilder.() -> Unit {
        return createLayersBuilderBlock(myLayerKind, mapObjectBuilders)
    }

    private fun processDataPoints(): List<MapObject> {
        val mapObjects = ArrayList<MapObject>(myAesthetics.dataPointCount())
        for (p in myAesthetics.dataPoints()) {
            dataPointToMapObject(p) { mapObjects.add(it) }
        }
        return mapObjects
    }

    private fun processMultiDataPoints(): List<MapObject> {
        val multiDataPoints = MultiDataPointHelper.getPoints(
            myAesthetics,
            getSortingMode(myLayerKind)
        )
        if (myLayerKind === MapLayerKind.BAR) {
            myMaxAbsValue = getMaxAbsValue(multiDataPoints)
        }

        val mapObjects = ArrayList<MapObject>(multiDataPoints.size)
        for (multiDataPoint in multiDataPoints) {
            multiDataPointToMapObject(multiDataPoint) { mapObjects.add(it) }
        }
        return mapObjects
    }

    private fun processDataPoints2(): List<MapObjectBuilder> {
        val mapObjects = ArrayList<MapObjectBuilder>(myAesthetics.dataPointCount())
        for (p in myAesthetics.dataPoints()) {
            mapObjects.add(MapObjectBuilder(p, myLayerKind).apply { setIfNeeded(p) })
        }
        return mapObjects
    }

    private fun processMultiDataPoints2(): List<MapObjectBuilder> {
        val multiDataPoints = MultiDataPointHelper.getPoints(
            myAesthetics,
            getSortingMode(myLayerKind)
        )
        if (myLayerKind === MapLayerKind.BAR) {
            myMaxAbsValue = getMaxAbsValue(multiDataPoints)
        }

        val mapObjects = ArrayList<MapObjectBuilder>(multiDataPoints.size)
        for (multiDataPoint in multiDataPoints) {
            mapObjects.add(MapObjectBuilder(multiDataPoint, myLayerKind).apply { setIfNeeded(multiDataPoint.aes) })
        }
        return mapObjects
    }

    private fun dataPointToMapObject(p: DataPointAesthetics, consumer: (MapObject) -> Unit) {
        createMapObject(p, MapObjectBuilder(p, myLayerKind), consumer)
    }

    private fun multiDataPointToMapObject(multiDataPoint: MultiDataPoint, consumer: (MapObject) -> Unit) {
        createMapObject(multiDataPoint.aes,
            MapObjectBuilder(multiDataPoint, myLayerKind), consumer)
    }

    private fun createMapObject(
        p: DataPointAesthetics,
        mapObjectBuilder: MapObjectBuilder,
        consumer: (MapObject) -> Unit
    ) {
        setGeometryPointIfNeeded(p, mapObjectBuilder)
        setStrokeWidthIfNeeded(mapObjectBuilder)
        setMaxAbsValueIfNeeded(mapObjectBuilder)

        mapObjectBuilder.build(consumer)
    }

    private fun MapObjectBuilder.setIfNeeded(
        p: DataPointAesthetics
    ) {
        setGeometryPointIfNeeded(p, this)
        // setStrokeWidthIfNeeded(this)
        // setMaxAbsValueIfNeeded(this)
        setGeodesicIfNeeded(this)
    }

    private fun setGeometryPointIfNeeded(p: DataPointAesthetics, mapObjectBuilder: MapObjectBuilder) {
        var lonlat: Vec<LonLat>? = null
        if (myLonLatInsideMapIdSpecified) {
            lonlat = LonLatParser.parse(p.mapId().toString())
        }

        if (lonlat != null) {
            mapObjectBuilder.setGeometryPoint(lonlat)
        }
    }

    private fun setMaxAbsValueIfNeeded(mapObjectBuilder: MapObjectBuilder) {
        if (myMaxAbsValue == null) {
            return
        }

        mapObjectBuilder.setMaxAbsValue(myMaxAbsValue)
    }

    private fun useMultiDataPoint(): Boolean {
        return myLayerKind === MapLayerKind.PIE || myLayerKind === MapLayerKind.BAR
    }

    private fun setStrokeWidthIfNeeded(mapObjectBuilder: MapObjectBuilder) {
        if (myStrokeWidth == null) {
            return
        }

        if (myLayerKind === POINT || myLayerKind === MapLayerKind.PIE || myLayerKind === MapLayerKind.BAR) {
            mapObjectBuilder.setStrokeWidth(myStrokeWidth)
        }
    }

    private fun setGeodesicIfNeeded(mapObjectBuilder: MapObjectBuilder) {
        if (myLayerKind == MapLayerKind.PATH) {
            mapObjectBuilder.geodesic = myGeodesic
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
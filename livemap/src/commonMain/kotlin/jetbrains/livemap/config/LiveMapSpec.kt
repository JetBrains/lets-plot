/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.livemap.api.LayersBuilder
import jetbrains.livemap.api.MapLocation
import jetbrains.livemap.core.projections.GeoProjection
import jetbrains.livemap.mapengine.basemap.BasemapTileSystemProvider
import jetbrains.livemap.ui.CursorService

class LiveMapSpec(
    val geocodingService: GeocodingService,
    val size: DoubleVector,
    val isScaled: Boolean,
    val isInteractive: Boolean,
    val isClustering: Boolean,
    val isLabels: Boolean,
    val isTiles: Boolean,
    val isUseFrame: Boolean,
    val geoProjection: GeoProjection,
    val location: MapLocation?,
    val zoom: Int?,
    val layers: List<LayersBuilder.() -> Unit>,
    val isLoopX: Boolean,
    val isLoopY: Boolean,
    val mapLocationConsumer: (DoubleRectangle) -> Unit,
    val basemapTileSystemProvider: BasemapTileSystemProvider,
    val attribution: String?,
    val showCoordPickTools: Boolean,
    val cursorService: CursorService,
    val minZoom: Int = MIN_ZOOM,
    val maxZoom: Int = MAX_ZOOM,
    val devParams: DevParams
)
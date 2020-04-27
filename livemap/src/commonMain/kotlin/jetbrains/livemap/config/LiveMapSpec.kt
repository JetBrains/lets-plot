/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.LayerProvider
import jetbrains.livemap.MapLocation
import jetbrains.livemap.core.projections.ProjectionType
import jetbrains.livemap.tiles.TileSystemProvider

class LiveMapSpec(
    val geocodingService: GeocodingService,
    val size: DoubleVector,
    val isScaled: Boolean,
    val isInteractive: Boolean,
    val isEnableMagnifier: Boolean,
    val isClustering: Boolean,
    val isLabels: Boolean,
    val isTiles: Boolean,
    val isUseFrame: Boolean,
    val projectionType: ProjectionType,
    val location: MapLocation?,
    val zoom: Int?,
    val level: FeatureLevel?,
    val parent: MapRegion?,
    val layerProvider: LayerProvider,
    val isLoopX: Boolean,
    val isLoopY: Boolean,
    val mapLocationConsumer: (DoubleRectangle) -> Unit,
    val tileSystemProvider: TileSystemProvider,
    val devParams: DevParams
)
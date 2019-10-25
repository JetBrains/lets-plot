package jetbrains.livemap

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.gis.geoprotocol.FeatureLevel
import jetbrains.gis.geoprotocol.GeocodingService
import jetbrains.gis.geoprotocol.MapRegion
import jetbrains.livemap.mapobjects.MapLayer
import jetbrains.livemap.projections.ProjectionType

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
    val layers: List<MapLayer>,
    val isLoopX: Boolean,
    val isLoopY: Boolean,
    val mapLocationConsumer: (DoubleRectangle) -> Unit,
    val devParams: DevParams
)
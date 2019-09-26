package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.MapProjection
import jetbrains.livemap.projections.ProjectionUtil
import jetbrains.livemap.projections.World

typealias LonLatGeometry = TypedGeometry<LonLat>
//class LonLatGeometry(geometry: TypedGeometry<LonLat>) : TypedGeometry<LonLat> by geometry {
//    companion object {
//        fun create(points: LonLatMultiPolygon) = LonLatGeometry(Geometry.create(points) as TypedGeometry<LonLat>)
//    }
//}

typealias WorldGeometry = TypedGeometry<World>
//class WorldGeometry(geometry: TypedGeometry<World>) : TypedGeometry<World> by geometry {
//    companion object {
//        fun create(points: MultiPolygon) = WorldGeometry(Geometry.create(points) as TypedGeometry<World>)
//    }
//}

typealias ClientGeometry = TypedGeometry<Client>
//class ClientGeometry(geometry: TypedGeometry<Client>) : TypedGeometry<Client> by geometry {
//    companion object {
//        fun create(points: TypedGeometry<Client>) = ClientGeometry(Geometry.create(points) as TypedGeometry<Client>)
//    }
//}

fun LonLatGeometry.toWorldGeometry(mapProjection: MapProjection): WorldGeometry {
    return asMultipolygon()
        .run { ProjectionUtil.transformMultipolygon(this, mapProjection::project) }
        .run { WorldGeometry.create(this) }
}
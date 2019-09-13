package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.LonLat
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.gis.geoprotocol.TypedGeometry
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.LonLatMultiPolygon
import jetbrains.livemap.projections.World

class LonLatGeometry(geometry: TypedGeometry<LonLat>) : TypedGeometry<LonLat> by geometry {
    companion object {
        fun create(points: LonLatMultiPolygon) = LonLatGeometry(Geometry.create(points) as TypedGeometry<LonLat>)
    }
}

class WorldGeometry(geometry: TypedGeometry<World>) : TypedGeometry<World> by geometry {
    companion object {
        fun create(points: MultiPolygon) = WorldGeometry(Geometry.create(points) as TypedGeometry<World>)
    }
}

class ClientGeometry(geometry: TypedGeometry<Client>) : TypedGeometry<Client> by geometry {
    companion object {
        fun create(points: MultiPolygon) = ClientGeometry(Geometry.create(points) as TypedGeometry<Client>)
    }
}

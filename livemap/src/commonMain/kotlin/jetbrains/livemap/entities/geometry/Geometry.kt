package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.projections.Client
import jetbrains.livemap.projections.LonLat
import jetbrains.livemap.projections.World

class LonLatGeometry(geometry: Geometry<LonLat>) : Geometry<LonLat> by geometry {
    companion object {
        fun create(points: MultiPolygon<LonLat>) = LonLatGeometry(Geometry.create(points))
    }
}

class WorldGeometry(geometry: Geometry<World>) : Geometry by geometry {
    companion object {
        fun create(points: MultiPolygon<World>) = WorldGeometry(Geometry.create(points))
    }
}

class ClientGeometry(geometry: Geometry) : Geometry by geometry {
    companion object {
        fun create(points: MultiPolygon<Client>) = ClientGeometry(Geometry.create(points))
    }
}

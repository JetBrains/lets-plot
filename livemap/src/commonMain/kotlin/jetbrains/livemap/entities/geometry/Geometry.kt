package jetbrains.livemap.entities.geometry

import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.gis.geoprotocol.Geometry

class LonLatGeometry(geometry: Geometry) : Geometry by geometry {
    companion object {
        fun create(points: MultiPolygon) = LonLatGeometry(Geometry.create(points))
    }
}

class WorldGeometry(geometry: Geometry) : Geometry by geometry {
    companion object {
        fun create(points: MultiPolygon) = WorldGeometry(Geometry.create(points))
    }
}

class ClientGeometry(geometry: Geometry) : Geometry by geometry {
    companion object {
        fun create(points: MultiPolygon) = ClientGeometry(Geometry.create(points))
    }
}

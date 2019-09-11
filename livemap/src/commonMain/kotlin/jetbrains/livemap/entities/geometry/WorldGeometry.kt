package jetbrains.livemap.entities.geometry

import jetbrains.gis.geoprotocol.Geometry

class LonLatGeometry(geometry: Geometry) : Geometry by geometry
class WorldGeometry(geometry: Geometry) : Geometry by geometry
class ClientGeometry(geometry: Geometry) : Geometry by geometry

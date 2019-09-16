package jetbrains.livemap.projections

interface MapProjection : Transform<LonLatPoint, WorldPoint> {
    val mapRect: WorldRectangle
}
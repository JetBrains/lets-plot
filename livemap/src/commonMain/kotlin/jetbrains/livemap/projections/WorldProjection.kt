package jetbrains.livemap.projections

class WorldProjection(
    zoom: Int
) : Transform<WorldPoint, ClientPoint> {
    override fun project(v: WorldPoint): ClientPoint {
        return projector.project(v)
    }

    override fun invert(v: ClientPoint): WorldPoint {
        return projector.invert(v)
    }

    private val projector: Transform<WorldPoint, ClientPoint> =
        ProjectionUtil.square(ProjectionUtil.zoom(zoom))
}
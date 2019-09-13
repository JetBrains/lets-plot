package jetbrains.livemap.projections

interface ViewProjectionHelper {
    fun normalizeX(x: Double): Double
    fun normalizeY(y: Double): Double

    fun getOrigins(objRect: WorldRectangle, viewRect: WorldRectangle): List<WorldPoint>
    fun getCells(viewRect: WorldRectangle, cellLevel: Int): Set<CellKey>
}
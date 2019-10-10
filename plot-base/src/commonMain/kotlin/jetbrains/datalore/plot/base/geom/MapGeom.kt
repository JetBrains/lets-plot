package jetbrains.datalore.plot.base.geom

class MapGeom : PolygonGeom() {
    companion object {
//        val RENDERS = listOf(
//
//                // auto-wired to 'x' or 'long' and to 'y' or 'lat'
//                Aes.X,
//                Aes.Y,
//
//                Aes.SIZE, // path width
//                Aes.LINETYPE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA,
//
//                Aes.MAP_ID
//        )

        const val HANDLES_GROUPS = true
    }
}

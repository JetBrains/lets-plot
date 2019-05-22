package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.visualization.plot.base.Aes

class MapGeom : PolygonGeom() {
    companion object {
        val RENDERS = listOf(

                // auto-wired to 'x' or 'long' and to 'y' or 'lat'
                Aes.X,
                Aes.Y,

                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,

                Aes.MAP_ID
        )

        const val HANDLES_GROUPS = true
    }
}

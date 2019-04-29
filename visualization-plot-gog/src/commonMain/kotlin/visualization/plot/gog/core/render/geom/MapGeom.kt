package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal class MapGeom : PolygonGeom() {
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

        val HANDLES_GROUPS = true
    }
}

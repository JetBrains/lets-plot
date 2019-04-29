package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.render.Aes

internal class HistogramGeom : BarGeom() {
    companion object {
        val RENDERS = listOf(
                Aes.X,
                Aes.Y,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                //Aes.WEIGHT,    // ToDo: this is actually handled by 'stat' (bin,count)
                Aes.WIDTH,
                Aes.SIZE
        )

        val HANDLES_GROUPS = false
    }
}

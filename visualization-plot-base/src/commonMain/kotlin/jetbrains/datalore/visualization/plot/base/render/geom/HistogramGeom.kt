package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.visualization.plot.base.Aes

class HistogramGeom : BarGeom() {
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

        const val HANDLES_GROUPS = false
    }
}

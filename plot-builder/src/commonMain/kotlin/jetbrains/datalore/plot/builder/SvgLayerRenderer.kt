package jetbrains.datalore.plot.builder

import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.render.SvgRoot
import jetbrains.datalore.visualization.plot.base.render.svg.SvgComponent

class SvgLayerRenderer(
        private val myAesthetics: Aesthetics,
        private val myGeom: Geom,
        private val myPos: PositionAdjustment,
        private val myCoord: CoordinateSystem,
        private val myGeomContext: GeomContext) : SvgComponent(), SvgRoot {

    override fun buildComponent() {
        buildLayer()
    }

    private fun buildLayer() {
        myGeom.build(this, myAesthetics, myPos, myCoord, myGeomContext)
    }
}

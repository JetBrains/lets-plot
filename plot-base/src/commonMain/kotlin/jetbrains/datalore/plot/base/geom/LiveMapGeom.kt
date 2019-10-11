package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GenericLegendKeyElementFactory
import jetbrains.datalore.plot.base.interact.MappedDataAccess
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.vis.canvasFigure.CanvasFigure


class LiveMapGeom(private val myDisplayMode: DisplayMode) : Geom {
    private var myMapProvider: LiveMapProvider? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() {
            return when (myDisplayMode) {
                DisplayMode.POINT -> PointLegendKeyElementFactory()
                DisplayMode.PIE -> FilledCircleLegendKeyElementFactory()
                else -> GenericLegendKeyElementFactory()
            }
        }

    override fun build(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        throw IllegalStateException("Not applicable to live map")
    }

    fun setLiveMapProvider(liveMapProvider: LiveMapProvider) {
        myMapProvider = liveMapProvider
    }

    fun createCanvasFigure(
        aesthetics: Aesthetics,
        dataAccess: MappedDataAccess,
        dinension: DoubleVector,
        layers: List<LiveMapLayerData>
    ): CanvasFigure {
        return myMapProvider?.createLiveMap(aesthetics, dataAccess, dinension, layers) ?: error("geom_livemap is not enabled")
    }

    companion object {

        // ToDo: not static, depends on 'display mode'
//        val RENDERS = listOf(
//                Aes.MAP_ID,
//                Aes.ALPHA,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.SIZE,
//                Aes.SHAPE,
//                Aes.FRAME,
//                Aes.X,
//                Aes.Y
//        )
        const val HANDLES_GROUPS = false
    }
}

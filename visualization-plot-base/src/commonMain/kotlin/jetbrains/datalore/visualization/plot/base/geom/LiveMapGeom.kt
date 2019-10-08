package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.geom.LiveMapProvider.LiveMapData
import jetbrains.datalore.visualization.plot.base.geom.util.GenericLegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.SvgRoot


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
        bounds: DoubleRectangle,
        layers: List<LiveMapLayerData>
    ): LiveMapData {
        return myMapProvider!!.createLiveMap(aesthetics, dataAccess, bounds, layers)
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

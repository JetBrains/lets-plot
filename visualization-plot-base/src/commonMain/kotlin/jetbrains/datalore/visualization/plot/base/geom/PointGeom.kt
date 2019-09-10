package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.aes.AestheticsUtil
import jetbrains.datalore.visualization.plot.base.geom.util.GeomHelper
import jetbrains.datalore.visualization.plot.base.geom.util.HintColorUtil.fromColorValue
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector.TooltipParams
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.SvgRoot
import jetbrains.datalore.visualization.plot.base.render.point.NamedShape
import jetbrains.datalore.visualization.plot.base.render.point.PointShapeSvg
import jetbrains.datalore.visualization.plot.base.render.point.TinyPointShape
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

open class PointGeom : GeomBase() {

    var animation: Any? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PointLegendKeyElementFactory()

    public override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val targetCollector = getGeomTargetCollector(ctx)

        val count = aesthetics.dataPointCount()
        val slimGroup = SvgSlimElements.g(count)
        for (i in 0 until count) {
            val p = aesthetics.dataPointAt(i)
            val x = p.x()
            val y = p.y()

            if (SeriesUtil.allFinite(x, y)) {
                val location = helper.toClient(DoubleVector(x!!, y!!), p)

                val shape = p.shape()!!
                targetCollector.addPoint(i, location, shape.size(p) / 2, getTooltipParams(p))
//                val o = shape.create(location, p)
                val o = PointShapeSvg.create(shape, location, p)
                o.appendTo(slimGroup)
            }
        }
        root.add(wrap(slimGroup))
    }

    private fun getTooltipParams(p: DataPointAesthetics): TooltipParams {
        var color = Color.TRANSPARENT
        if (p.shape() == TinyPointShape) {
            color = p.color()!!
        } else if (p.shape() is NamedShape) {
            val shape = p.shape() as NamedShape
            color = AestheticsUtil.fill(shape.isFilled, shape.isSolid, p)
        }

        return params().setColor(fromColorValue(color, p.alpha()!!))
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.X,
//                Aes.Y,
//                Aes.SIZE,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.ALPHA,
//                Aes.SHAPE,
//                Aes.MAP_ID
//                // strokeWidth
//        )

        const val HANDLES_GROUPS = false
    }
}


package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimGroup
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetCollector
import jetbrains.datalore.visualization.plot.base.render.*
import jetbrains.datalore.visualization.plot.base.render.geom.util.GenericLegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.svg.LinePath
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

abstract class GeomBase : Geom {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = GenericLegendKeyElementFactory()

    override fun build(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        buildIntern(root, aesthetics, pos, coord, ctx)
    }

    protected fun getGeomTargetCollector(ctx: GeomContext): GeomTargetCollector {
        return ctx.targetCollector
    }

    protected abstract fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext)

    companion object {
        fun wrap(slimGroup: SvgSlimGroup): SvgGElement {
            val g = SvgGElement()
            g.isPrebuiltSubtree = true
            g.children().add(slimGroup.asDummySvgNode())
            return g
        }

        fun aesViewPort(aesthetics: Aesthetics): DoubleRectangle {
            return rect(
                    aesthetics.overallRange(Aes.X),
                    aesthetics.overallRange(Aes.Y))
        }

        fun aesBoundingBox(aesthetics: Aesthetics): DoubleRectangle {
            return rect(
                    aesthetics.range(Aes.X)!!,
                    aesthetics.range(Aes.Y)!!)
        }

        private fun rect(rangeX: ClosedRange<Double>, rangeY: ClosedRange<Double>): DoubleRectangle {
            return DoubleRectangle(
                    rangeX.lowerEndpoint(), rangeY.lowerEndpoint(),
                    SeriesUtil.span(rangeX), SeriesUtil.span(rangeY))
        }

        fun appendNodes(paths: List<LinePath>, root: SvgRoot) {
            for (path in paths) {
                root.add(path.rootGroup)
            }
        }
    }
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.legend.GenericLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.svg.LinePath
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimObject

abstract class GeomBase : Geom {

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = GenericLegendKeyElementFactory()

    override fun build(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        buildIntern(root, aesthetics, pos, coord, ctx)
    }

    open fun preferableNullDomain(aes: Aes<*>): DoubleSpan {
        return DoubleSpan(-0.5, 0.5)
    }

    protected fun getGeomTargetCollector(ctx: GeomContext): GeomTargetCollector {
        return ctx.targetCollector
    }

    protected abstract fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    )

    companion object {
        fun wrap(slimGroup: SvgSlimGroup): SvgGElement {
            val g = SvgGElement()
            g.isPrebuiltSubtree = true
            g.children().add(slimGroup.asDummySvgNode())
            return g
        }

        fun wrap(o: SvgSlimObject): SvgGElement {
            val slimGroup = SvgSlimElements.g(1)
            o.appendTo(slimGroup)
            return wrap(slimGroup)
        }

        fun overallAesBounds(ctx: GeomContext): DoubleRectangle {
            return when {
                ctx.flipped -> ctx.getAesBounds().flip()
                else -> ctx.getAesBounds()
            }
        }

        fun layerAesBounds(aesthetics: Aesthetics): DoubleRectangle {
            // ToDo: flip?
            return DoubleRectangle(
                aesthetics.range(Aes.X)!!,
                aesthetics.range(Aes.Y)!!
            )
        }

        fun SvgRoot.appendNodes(paths: List<LinePath>) {
            for (path in paths) {
                add(path.rootGroup)
            }
        }
    }
}

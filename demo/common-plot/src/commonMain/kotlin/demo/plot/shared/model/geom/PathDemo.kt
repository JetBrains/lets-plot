/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.geom

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.array
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.list
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.geom.PathGeom
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent

open class PathDemo : SimpleDemoBase() {

    fun createModels(): List<GroupComponent> {
        return listOf(
            simple(),
            grouped()
        )
    }

    private fun simple(): GroupComponent {
        val count = 5
        val x = arrayOf(300.0, 500.0, 400.0, 300.0, 500.0)
        val y = arrayOf(-100.0, -100.0, 0.0, 100.0, 100.0)

        // layer
        val aes = AestheticsBuilder(count)
            .x(array(x))
            .y(array(y))
            .color(constant(Color.RED))
            .build()

        return createGeomLayer(aes)
    }

    private fun grouped(): GroupComponent {
        val xA = arrayOf(300.0, 500.0, 400.0, 300.0, 500.0)
        val yA = arrayOf(-100.0, -100.0, 0.0, 100.0, 100.0)
        val xB = arrayOf(310.0, 510.0, 410.0, 310.0, 510.0)
        val yB = arrayOf(-90.0, -90.0, 10.0, 110.0, 110.0)

        val x = ArrayList<Double>()
        val y = ArrayList<Double>()
        val group = ArrayList<Int>()
        for (i in xA.indices) {
            x.add(xA[i])
            y.add(yA[i])
            group.add(0)

            x.add(xB[i])
            y.add(yB[i])
            group.add(1)
        }

        val colorGen = { index: Int ->
            if (group[index] == 0)
                Color.BLUE
            else
                Color.DARK_MAGENTA
        }

        // layer
        val aes = AestheticsBuilder(x.size)
            .x(list(x))
            .y(list(y))
            .color(colorGen)
            .group(list(group))
            .build()

        return createGeomLayer(aes)
    }

    private fun createGeomLayer(aes: Aesthetics): GroupComponent {
        val groupComponent = GroupComponent()
        val domainX = aes.range(Aes.X)!!
        val domainY = aes.range(Aes.Y)!!
        val coord = Coords.DemoAndTest.create(
            DoubleSpan(domainX.lowerEnd - 20, domainX.upperEnd + 20),
            DoubleSpan(domainY.lowerEnd - 20, domainY.upperEnd + 20),
            demoInnerSize
        )

        val layer = org.jetbrains.letsPlot.core.plot.builder.SvgLayerRenderer(
            aes,
            PathGeom(),
            PositionAdjustments.identity(),
            coord,
            EMPTY_GEOM_CONTEXT
        )
        groupComponent.add(layer.rootGroup)
        return groupComponent
    }
}

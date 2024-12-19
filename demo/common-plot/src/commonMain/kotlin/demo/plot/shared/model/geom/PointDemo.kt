/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.geom

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.array
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsBuilder.Companion.constant
import org.jetbrains.letsPlot.core.plot.base.coord.Coords
import org.jetbrains.letsPlot.core.plot.base.geom.PointGeom
import org.jetbrains.letsPlot.core.plot.base.pos.PositionAdjustments
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent

open class PointDemo : SimpleDemoBase() {

    fun createModels(): List<GroupComponent> {
        return listOf(
            simple()
        )
    }

    private fun simple(): GroupComponent {
        val count = 5
        val x = arrayOf(300.0, 500.0, 400.0, 300.0, 500.0)
        val y = arrayOf(-100.0, -100.0, 0.0, 100.0, 100.0)
        val size = arrayOf(100.0, 1.0, 10.0, 100.0, 1.0)
        /*
    for (int i = 0; i < size.length; i++) {
      size[i] = size[i] * 64.;
    }
*/

        // layer
        val aes = AestheticsBuilder(count)
            .x(array(x))
            .y(array(y))
            .color(constant(Color.RED))
            .shape(constant(NamedShape.FILLED_CIRCLE))
            .size(array(size))
            .build()

        val groupComponent = GroupComponent()

        val coord = Coords.DemoAndTest.create(
            DoubleSpan(x.min() - 20, x.max() + 20),
            DoubleSpan(y.min() - 20, y.max() + 20),
            demoInnerSize
        )
        val layer = org.jetbrains.letsPlot.core.plot.builder.SvgLayerRenderer(
            aes,
            PointGeom(),
            PositionAdjustments.identity(),
            coord,
            EMPTY_GEOM_CONTEXT
        )
        groupComponent.add(layer.rootGroup)
        return groupComponent
    }
}

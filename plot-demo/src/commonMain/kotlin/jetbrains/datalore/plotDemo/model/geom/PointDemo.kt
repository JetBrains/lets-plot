/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.geom

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.geom.PointGeom
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

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
        val layer = jetbrains.datalore.plot.builder.SvgLayerRenderer(
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

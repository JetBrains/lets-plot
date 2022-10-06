/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.geom.BarGeom
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.plotDemo.model.util.DemoUtil

open class BarDemo : SimpleDemoBase() {

    fun createModels(): List<GroupComponent> {
        return listOf(
                simple(),
                dodgePos(),
                stackPos()
        )
    }

    private fun x6(): Array<Double> {
        return arrayOf(300.0, 300.0, 400.0, 400.0, 500.0, 500.0)
    }

    private fun y6(): Array<Double> {
        return arrayOf(-50.0, -30.0, 50.0, 45.0, -10.0, 110.0)
    }

    private fun color6(): Array<Color> {
        return arrayOf(Color.DARK_BLUE, Color.DARK_GREEN, Color.DARK_BLUE, Color.DARK_GREEN, Color.DARK_BLUE, Color.DARK_GREEN)
    }


    private fun simple(): GroupComponent {
        val count = 3
        val x = arrayOf(300.0, 400.0, 500.0)
        val y = arrayOf(-50.0, 50.0, 100.0)

        // layer
        val aes = AestheticsBuilder(count)
                .x(array(x))
                .y(array(y))
                .fill(constant(Color.DARK_BLUE))
                .width(constant(0.75))
                .build()

        val pos = PositionAdjustments.dodge(aes, 1, 0.75)
        return createGeomLayer(aes, pos)
    }

    private fun dodgePos(): GroupComponent {
        val count = 6
        val groupCount = 2
        val group = arrayOf(0, 1, 0, 1, 0, 1)

        // layer
        val aes = AestheticsBuilder(count)
                .x(array(x6()))
                .y(array(y6()))
                .fill(array(color6()))
                .width(constant(0.9))
                .group(array(group))
                .build()

        val bandWidthRatio = 0.75
        val pos = PositionAdjustments.dodge(aes, groupCount, bandWidthRatio)
        return createGeomLayer(aes, pos)
    }

    private fun stackPos(): GroupComponent {
        val count = 6

        // layer
        val aes = AestheticsBuilder(count)
                .x(array(x6()))
                .y(array(y6()))
                .fill(array(color6()))
                .width(constant(0.75))
                .build()

        val pos = PositionAdjustments.stack(aes, PositionAdjustments.StackingStrategy.SPLIT_POSITIVE_NEGATIVE, vjust = null)
        return createGeomLayer(aes, pos)
    }

    private fun createGeomLayer(aes: Aesthetics, pos: PositionAdjustment): GroupComponent {
        val groupComponent = GroupComponent()
        val coord = Coords.create(DoubleVector(0.0, demoInnerSize.y / 2))
        val layer =
            jetbrains.datalore.plot.builder.SvgLayerRenderer(aes,
                BarGeom(), pos, coord, DemoUtil.geomContext(aes))
        groupComponent.add(layer.rootGroup)
        return groupComponent
    }
}

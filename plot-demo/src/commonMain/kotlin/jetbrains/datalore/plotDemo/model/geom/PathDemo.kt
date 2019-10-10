package jetbrains.datalore.plotDemo.model.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.datalore.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.plot.base.coord.Coords
import jetbrains.datalore.plot.base.geom.PathGeom
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.render.svg.GroupComponent
import jetbrains.datalore.plotDemo.model.SimpleDemoBase

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
                .x(collection(x))
                .y(collection(y))
                .color(colorGen)
                .group(collection(group))
                .build()

        return createGeomLayer(aes)
    }

    private fun createGeomLayer(aes: Aesthetics): GroupComponent {
        val groupComponent = GroupComponent()
        val coord = Coords.create(DoubleVector(0.0, demoInnerSize.y / 2))
        val layer = jetbrains.datalore.plot.builder.SvgLayerRenderer(
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

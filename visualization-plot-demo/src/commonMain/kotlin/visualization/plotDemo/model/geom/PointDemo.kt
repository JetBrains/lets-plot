package jetbrains.datalore.visualization.plotDemo.model.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder.Companion.array
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.visualization.plot.base.coord.Coords
import jetbrains.datalore.visualization.plot.base.render.geom.PointGeom
import jetbrains.datalore.visualization.plot.base.render.point.NamedShape
import jetbrains.datalore.visualization.plot.base.render.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.base.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.gog.plot.SvgLayerRenderer
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase

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

        val coord = Coords.create(DoubleVector(0.0, demoInnerSize.y / 2))
        val layer = SvgLayerRenderer(aes, PointGeom(), PositionAdjustments.identity(), coord, EMPTY_GEOM_CONTEXT)
        groupComponent.add(layer.rootGroup)
        return groupComponent
    }
}

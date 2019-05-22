package jetbrains.datalore.visualization.plotDemo.model.stat

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder.Companion.collection
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder.Companion.constant
import jetbrains.datalore.visualization.plot.base.coord.Coords
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.base.data.SimpleStatContext
import jetbrains.datalore.visualization.plot.base.data.stat.Stats
import jetbrains.datalore.visualization.plot.base.render.geom.BarGeom
import jetbrains.datalore.visualization.plot.base.render.geom.PointGeom
import jetbrains.datalore.visualization.plot.base.render.point.NamedShape
import jetbrains.datalore.visualization.plot.base.render.pos.PositionAdjustments
import jetbrains.datalore.visualization.plot.base.render.svg.GroupComponent
import jetbrains.datalore.visualization.plot.base.scale.Mappers
import jetbrains.datalore.visualization.plot.base.scale.Scales
import jetbrains.datalore.visualization.plot.builder.SvgLayerRenderer
import jetbrains.datalore.visualization.plotDemo.model.SimpleDemoBase
import jetbrains.datalore.visualization.plotDemo.model.util.DemoUtil

open class BinDemo : SimpleDemoBase() {

    fun createModels(): List<GroupComponent> {
        return listOf(
                histogram(),
                histogramWithLimits()
        )
    }


    private fun histogram(): GroupComponent = createModel(false)

    private fun histogramWithLimits(): GroupComponent = createModel(true)

    private fun createModel(limits: Boolean): GroupComponent {
        val coord = Coords.create(DoubleVector(demoInnerSize.x / 2, demoInnerSize.y / 2))

        val count = 200

        val x = DemoUtil.gauss(count, 32, 0.0, 100.0)
        val y = DemoUtil.gauss(count, 64, 0.0, 50.0)

        var scaleX = Scales.continuousDomainNumericRange("A scale")
                .with()
                .mapper(Mappers.mul(1.0))
                .build()

        if (limits) {
            scaleX = scaleX.with()
                    .lowerLimit(-100.0)
                    .upperLimit(100.0)
                    .build()
        }

        val groupComponent = GroupComponent()

        // bin stat/bar geom layer
        run {
            val varA = DataFrame.Variable("A")
            val varB = DataFrame.Variable("B")
            var data = DataFrame.Builder()
                    .putNumeric(varA, x)
                    .putNumeric(varB, y)
                    .build()

            val scaleY = Scales.continuousDomainNumericRange("bar height")
                    .with()
                    .mapper(Mappers.mul(2.5))
                    .build()

            // transform must happen before stat
            data = DataFrameUtil.applyTransform(data, varA, Aes.X, scaleX)
            data = DataFrameUtil.applyTransform(data, varB, Aes.Y, scaleY)

            // stat uses transform vars
            val binCount = 10

            val stat = Stats.bin()
                    .binCount(binCount)
                    .build()
            data = stat.apply(data, SimpleStatContext(data))

            val statX = data.getNumeric(Stats.X)
            val statY = data.getNumeric(Stats.COUNT)


            // build aesthetics for stat summary
            run {
                val aes = AestheticsBuilder(statX.size)
                        .x(AestheticsBuilder.listMapper(statX, scaleX.mapper))
                        .y(AestheticsBuilder.listMapper(statY, scaleY.mapper))
                        .fill(constant(Color.LIGHT_BLUE))
                        .width(constant(.95))
                        .build()

                val pos = PositionAdjustments.dodge(aes, 1, .95)
                val layer = SvgLayerRenderer(aes, BarGeom(), pos, coord, DemoUtil.geomContext(aes))
                groupComponent.add(layer.rootGroup)
            }

            // add layer of stat points (for test)
            run {
                val aes = AestheticsBuilder(statX.size)
                        .x(AestheticsBuilder.listMapper(statX, scaleX.mapper))
                        .y(AestheticsBuilder.listMapper(statY, scaleY.mapper))
                        .color(constant(Color.BLUE))
                        .shape(constant(NamedShape.STICK_CIRCLE))
                        .size(constant(3.0))
                        .build()

                val pos = PositionAdjustments.identity()
                val layer = SvgLayerRenderer(aes, PointGeom(), pos, coord, EMPTY_GEOM_CONTEXT)
                groupComponent.add(layer.rootGroup)
            }
        }

        // points layer
        run {
            val aes = AestheticsBuilder(count)
                    .x(AestheticsBuilder.listMapper(x, scaleX.mapper))
                    .y(collection(y))
                    .color(constant(Color.RED))
                    .shape(constant(NamedShape.STICK_CIRCLE))
                    .size(constant(3.0))
                    .build()

            val pos = PositionAdjustments.identity()
            val layer = SvgLayerRenderer(aes, PointGeom(), pos, coord, EMPTY_GEOM_CONTEXT)
            groupComponent.add(layer.rootGroup)
        }


        return groupComponent
    }
}

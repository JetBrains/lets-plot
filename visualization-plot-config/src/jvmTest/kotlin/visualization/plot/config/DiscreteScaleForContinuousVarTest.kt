package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.config.TestUtil.assertClientWontFail
import jetbrains.datalore.visualization.plot.parsePlotSpec
import jetbrains.datalore.visualization.plot.server.config.ServerSideTestUtil.serverTransformWithoutEncoding
import kotlin.test.Test
import kotlin.test.assertEquals

class DiscreteScaleForContinuousVarTest {
    @Test
    fun keepLayerDataIfIdentityStat() {
        val data = "{" +
                "  'time': ['Lunch','Lunch', 'Dinner', 'Dinner', 'Dinner']" +
                "}"

        val spec = "{" +
                "   'data': " + data +
                "           ," +
                "   'mapping': {" +
                "             'x': 'time'," +
                "             'y': '..count..'," +
                "             'fill': '..count..'" +
                "           }," +
                "   'layers': [" +
                "               {" +
                "                  'geom':  {" +
                "                             'name': 'bar'" +
                "                           }" +
                "               }" +
                "           ]" +

                "   ," +
                "   'scales': [" +
                "               {" +
                "                  'aesthetic': 'fill'," +
                "                  'discrete': true," +
                "                  'values': ['#000000','#FFFFFF']" +
                //"                  'scale_mapper_kind': 'color_hue'" +
                "               }" +
                "           ]" +
                "}"

        val opts = parsePlotSpec(spec)
        val opts1 = serverTransformWithoutEncoding(opts)

        val plotConfigClientSide = assertClientWontFail(opts1)
        assertEquals(1, plotConfigClientSide.layerConfigs.size.toLong())

        val layerConfig = plotConfigClientSide.layerConfigs[0]
        val varBindings = layerConfig.varBindings
        var fillBinding: VarBinding? = null
        for (varBinding in varBindings) {
            if (varBinding.aes == Aes.FILL) {
                fillBinding = varBinding
                break
            }
        }
        val scale = fillBinding!!.scale as Scale<Color>?
        // this is discrete scale so input value for mapper is index
        // ..count.. [0] = 2   (two lunched)
        // ..count.. [1] = 3   (three dinners)
        val color0 = scale!!.mapper(0.0)
        val color1 = scale.mapper(1.0)

        assertEquals(Color.BLACK, color0)
        assertEquals(Color.WHITE, color1)
    }
}

package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.DemoAndTest
import jetbrains.datalore.visualization.plot.gog.config.TestUtil.assertClientWontFail
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.server.config.ServerSideTestUtil.serverTransformWithoutEncoding
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

        val opts = DemoAndTest.parseJson(spec)
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
        val scale = fillBinding!!.scale as Scale2<Color>?
        // this is discrete scale so input value for mapper is index
        // ..count.. [0] = 2   (two lunched)
        // ..count.. [1] = 3   (three dinners)
        val color0 = scale!!.mapper(0.0)
        val color1 = scale.mapper(1.0)

        assertEquals(Color.BLACK, color0)
        assertEquals(Color.WHITE, color1)
    }
}

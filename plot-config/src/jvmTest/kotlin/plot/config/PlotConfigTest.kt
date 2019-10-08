package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.Scale.CONTINUOUS_TRANSFORM
import jetbrains.datalore.plot.config.Option.Scale.NAME
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals

class PlotConfigTest {
    @Test
    fun scaleOptionsAreMergedForTheSameAes() {
        val spec = "{" +
                "   'scales': [" +
                "               {" +
                "                  'aesthetic': 'x'," +
                "                  'name': 'name_test'" +
                "               }" +
                "               ," +
                "               {" +
                "                  'aesthetic': 'x'," +
                "                  'trans': 'log10'" +
                "               }" +
                "           ]" +
                "}"

        val opts = parsePlotSpec(spec)
        val plotConfig = PlotConfigClientSide.create(opts)

        val scaleConfigs = plotConfig.createScaleConfigs()
        assertEquals(1, scaleConfigs.size.toLong())
        assertEquals("name_test", scaleConfigs[0].getString(NAME))
        assertEquals("log10", scaleConfigs[0].getString(CONTINUOUS_TRANSFORM))
    }
}
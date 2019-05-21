package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.stat.Stats
import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.base.scale.Scales
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.builder.scale.ScaleProviderHelper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GeomLayerBuilderTest {
    private fun checkBoundDataSize(data: DataFrame, binding: VarBinding, size: Int) {
        assertTrue(data.has(binding.variable), "has " + binding.variable)
        assertEquals(size, data[binding.variable].size, "size " + binding.variable)
    }

    private fun checkNotOriginalVar(data: DataFrame, binding: VarBinding) {
        assertTrue(data.has(binding.variable), "has " + binding.variable)
        assertFalse(binding.variable.isOrigin, "not original " + binding.variable)
    }


    @Test
    fun buildHistogram() {
        /*
        x=[0,1,0,1]
        cat = ['a','a','b','b']
        data = dict(x=x,cat=cat)
        ggplot(data) + geom_histogram(aes(x='x',fill='cat'))
        */
        val X = DataFrame.Variable("x")
        val cat = DataFrame.Variable("cat")
        val data = DataFrame.Builder()
                .put(X, listOf(0.0, 1.0, 0.0, 1.0))
                .put(cat, listOf("a", "a", "b", "b"))
                .build()

        val geomProvider = GeomProvider.histogram()
        val stat = Stats.bin().build()
        val posProvider = PosProvider.barStack()

        val bindings = ArrayList<VarBinding>()
        bindings.add(VarBinding(X, Aes.X, Scales.continuousDomain("x", Aes.X)))
        bindings.add(VarBinding(cat, Aes.FILL, ScaleProviderHelper.createDefault(Aes.FILL).createScale(data, cat)))

        val histogramLayer = GeomLayerBuilder.demoAndTest()
                .stat(stat)
                .geom(geomProvider)
                .pos(posProvider)
//                .addConstantAes(Aes.ALPHA, 0.5)
                .addBinding(bindings[0])
                .addBinding(bindings[1])
                .build(data)


        assertTrue(histogramLayer.hasBinding(Aes.X))
        assertTrue(histogramLayer.hasBinding(Aes.Y))
        assertTrue(histogramLayer.hasBinding(Aes.FILL))

        val layerData = histogramLayer.dataFrame

        checkBoundDataSize(layerData, histogramLayer.getBinding(Aes.X), 60)
        checkBoundDataSize(layerData, histogramLayer.getBinding(Aes.Y), 60)
        checkBoundDataSize(layerData, histogramLayer.getBinding(Aes.FILL), 60)

        checkNotOriginalVar(layerData, histogramLayer.getBinding(Aes.X))
        checkNotOriginalVar(layerData, histogramLayer.getBinding(Aes.Y))
        checkNotOriginalVar(layerData, histogramLayer.getBinding(Aes.FILL))
    }
}
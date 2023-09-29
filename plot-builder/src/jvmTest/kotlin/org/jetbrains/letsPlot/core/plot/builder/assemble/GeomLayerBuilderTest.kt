/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DiscreteTransform
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.stat.Stats
import org.jetbrains.letsPlot.core.plot.builder.VarBinding
import org.jetbrains.letsPlot.core.plot.builder.assemble.geom.GeomProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.DefaultMapperProvider
import org.jetbrains.letsPlot.core.plot.builder.scale.ScaleProviderHelper
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
        val stat = Stats.bin()
        val posProvider = PosProvider.barStack()

        val scaleProvider = ScaleProviderHelper.createDefault(org.jetbrains.letsPlot.core.plot.base.Aes.FILL)
        val scaleFill = scaleProvider.createScale(
            "cat",
            DiscreteTransform(data.distinctValues(cat), emptyList())
        )
        val scaleByAes = mapOf<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Scale>(
            org.jetbrains.letsPlot.core.plot.base.Aes.X to Scales.DemoAndTest.continuousDomain(
                "x",
                org.jetbrains.letsPlot.core.plot.base.Aes.X
            ),
            org.jetbrains.letsPlot.core.plot.base.Aes.Y to Scales.DemoAndTest.continuousDomain(
                "y",
                org.jetbrains.letsPlot.core.plot.base.Aes.Y
            ),
            org.jetbrains.letsPlot.core.plot.base.Aes.FILL to scaleFill
        )

        val scaleMappersNP: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, ScaleMapper<*>> = mapOf(
//            Aes.FILL to scaleProvider.mapperProvider.createDiscreteMapper(scaleFill.transform as DiscreteTransform)
            org.jetbrains.letsPlot.core.plot.base.Aes.FILL to DefaultMapperProvider[org.jetbrains.letsPlot.core.plot.base.Aes.FILL].createDiscreteMapper(
                scaleFill.transform as DiscreteTransform
            )
        )

        val bindings = ArrayList<VarBinding>()
        bindings.add(VarBinding(X, org.jetbrains.letsPlot.core.plot.base.Aes.X))
        bindings.add(VarBinding(cat, org.jetbrains.letsPlot.core.plot.base.Aes.FILL))

        val histogramLayer = GeomLayerBuilder.demoAndTest(
            geomProvider,
            stat,
            posProvider
        )
//            .stat(stat)
//            .geom(geomProvider)
//            .pos(posProvider)
//                .addConstantAes(Aes.ALPHA, 0.5)
            .addBinding(bindings[0])
            .addBinding(bindings[1])
            .build(data, scaleByAes, scaleMappersNP)


        assertTrue(histogramLayer.hasBinding(org.jetbrains.letsPlot.core.plot.base.Aes.X))
        // GeomLayerBuilder is no more responsible for creating 'stst' bindings
        // ToDo: this should be a plot test
//        assertTrue(histogramLayer.hasBinding(Aes.Y))
        assertTrue(histogramLayer.hasBinding(org.jetbrains.letsPlot.core.plot.base.Aes.FILL))

        val layerData = histogramLayer.dataFrame

        checkBoundDataSize(layerData, histogramLayer.getBinding(org.jetbrains.letsPlot.core.plot.base.Aes.X), 60)
//        checkBoundDataSize(layerData, histogramLayer.getBinding(Aes.Y), 60)
        checkBoundDataSize(layerData, histogramLayer.getBinding(org.jetbrains.letsPlot.core.plot.base.Aes.FILL), 60)

        checkNotOriginalVar(layerData, histogramLayer.getBinding(org.jetbrains.letsPlot.core.plot.base.Aes.X))
//        checkNotOriginalVar(layerData, histogramLayer.getBinding(Aes.Y))
        checkNotOriginalVar(layerData, histogramLayer.getBinding(org.jetbrains.letsPlot.core.plot.base.Aes.FILL))
    }

}
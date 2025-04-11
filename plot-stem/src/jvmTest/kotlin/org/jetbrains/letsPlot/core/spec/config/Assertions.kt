/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.Scale
import org.jetbrains.letsPlot.core.plot.base.ScaleMapper
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.spec.back.BackendTestUtil
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontend
import org.jetbrains.letsPlot.core.spec.front.PlotConfigFrontendUtil
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

fun transformToClientPlotConfig(spec: String): PlotConfigFrontend {
    return transformToClientPlotConfig(parsePlotSpec(spec))
}

fun transformToClientPlotConfig(plotSpec: MutableMap<String, Any>): PlotConfigFrontend {
    return plotSpec
        .let(BackendTestUtil::backendSpecTransform)
        .also { require(!PlotConfig.isFailure(it)) { PlotConfig.getErrorMessage(it) } }
        .let(TestUtil::createPlotConfigFrontend)
}

fun failedTransformToClientPlotConfig(spec: String): String {
    return parsePlotSpec(spec)
        .let(BackendTestUtil::backendSpecTransform)
        .let {
            try {
                PlotConfigFrontend.create(it) {}
            } catch (e: Throwable) {
                return@let e.localizedMessage as String
            }
            fail("Error expected")
        }
}

fun PlotConfigFrontend.assertValue(variable: String, values: List<*>): PlotConfigFrontend {
    val data = layerConfigs.single().combinedData
    assertEquals(values, data.get(DataFrameUtil.variables(data)[variable]!!))
    return this
}

fun PlotConfigFrontend.assertVariable(
    varName: String,
    isDiscrete: Boolean,
    msg: () -> String = { "" }
): PlotConfigFrontend {
    val layer = layerConfigs.single()
    if (!DataFrameUtil.hasVariable(layer.combinedData, varName)) {
        fail("Variable $varName is not found in ${layer.combinedData.variables().map(DataFrame.Variable::name)}")
    }
    val dfVar = DataFrameUtil.findVariableOrFail(layer.combinedData, varName)
    assertEquals(isDiscrete, layer.combinedData.isDiscrete(dfVar), msg())
    return this
}

fun PlotConfigFrontend.assertScale(
    aes: Aes<*>,
    isDiscrete: Boolean,
    name: String? = null,
    msg: () -> String = { "" }
): PlotConfigFrontend {
    val scale = createScales().getValue(aes)
    assertEquals(!isDiscrete, scale.isContinuous, msg())
    name?.let { assertEquals(it, scale.name, msg()) }
    return this
}

fun PlotConfigFrontend.createScales(): Map<Aes<*>, Scale> {
    val (_, scaleByAes) = PlotConfigFrontendUtil.createMappersAndScalesBeforeFacets(this)
    return scaleByAes
}

fun PlotConfigFrontend.createScaleMappers(): Map<Aes<*>, ScaleMapper<*>> {
    val (mapperByAes, _) = PlotConfigFrontendUtil.createMappersAndScalesBeforeFacets(this)
    return mapperByAes
}

fun PlotConfigFrontend.hasVariable(variable: DataFrame.Variable): PlotConfigFrontend {
    val layer = layerConfigs.single()
    assertTrue(DataFrameUtil.hasVariable(layer.combinedData, variable.name))
    return this
}

fun PlotConfigFrontend.assertBinding(
    aes: Aes<*>,
    varName: String
): PlotConfigFrontend {
    val layer = layerConfigs.single()

    val aesBindings = layer.varBindings.associateBy { it.aes }
    aesBindings[aes]
        ?.let { binding -> assertEquals(varName, binding.variable.name) }
        ?: fail("Binding for aes $aes was not found")
    return this
}

fun PlotConfigFrontend.assertNoBinding(
    aes: Aes<*>
): PlotConfigFrontend {
    val layer = layerConfigs.single()

    assert(layer.varBindings.none { it.aes == aes }) { "Binding for aes $aes was found" }
    return this
}


/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.data.DataFrameUtil
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

fun transformToClientPlotConfig(spec: String): PlotConfigClientSide {
    return parsePlotSpec(spec)
        .let(ServerSideTestUtil::serverTransformWithoutEncoding)
        .also { require(!PlotConfig.isFailure(it)) { PlotConfig.getErrorMessage(it) } }
        .let(TestUtil::assertClientWontFail)
}

fun failedTransformToClientPlotConfig(spec: String): String {
    return parsePlotSpec(spec)
        .let(ServerSideTestUtil::serverTransformWithoutEncoding)
        .let {
            try {
                PlotConfigClientSide.create(it)
            } catch (e: Throwable) {
                return@let e.localizedMessage as String
            }
            fail("Error expected")
        }
}

fun PlotConfigClientSide.assertValue(variable: String, values: List<*>): PlotConfigClientSide {
    val data = layerConfigs.single().combinedData
    assertEquals(values, data.get(DataFrameUtil.variables(data)[variable]!!))
    return this
}

fun PlotConfigClientSide.assertVariable(
    varName: String,
    isDiscrete: Boolean,
    msg: () -> String = { "" }
): PlotConfigClientSide {
    val layer = layerConfigs.single()
    if (!DataFrameUtil.hasVariable(layer.combinedData, varName)) {
        fail("Variable $varName is not found in ${layer.combinedData.variables().map(DataFrame.Variable::name)}")
    }
    val dfVar = DataFrameUtil.findVariableOrFail(layer.combinedData, varName)
    assertEquals(!isDiscrete, layer.combinedData.isNumeric(dfVar), msg())
    return this
}

fun PlotConfigClientSide.assertScale(
    aes: Aes<*>,
    isDiscrete: Boolean,
    name: String? = null,
    msg: () -> String = { "" }
): PlotConfigClientSide {
    val layer = layerConfigs.single()
    val binding = layer.varBindings.firstOrNull { it.aes == aes } ?: fail("$aes not found. ${msg()}")
    assertEquals(!isDiscrete, binding.scale!!.isContinuous)
    name?.let { assertEquals(it, binding.scale!!.name) }
    return this
}

fun PlotConfigClientSide.hasVariable(variable: DataFrame.Variable): PlotConfigClientSide {
    val layer = layerConfigs.single()
    assertTrue(DataFrameUtil.hasVariable(layer.combinedData, variable.name))
    return this
}

fun PlotConfigClientSide.assertBinding(
    aes: Aes<*>,
    varName: String
): PlotConfigClientSide {
    val layer = layerConfigs.single()

    val aesBindings = layer.varBindings.associateBy { it.aes }
    aesBindings[aes]
        ?.let { binding -> assertEquals(varName, binding.variable.name) }
        ?: fail("Binding for aes $aes was not found")
    return this
}

fun PlotConfigClientSide.assertNoBinding(
    aes: Aes<*>
): PlotConfigClientSide {
    val layer = layerConfigs.single()

    assert(layer.varBindings.none { it.aes == aes }) { "Binding for aes $aes was found" }
    return this
}


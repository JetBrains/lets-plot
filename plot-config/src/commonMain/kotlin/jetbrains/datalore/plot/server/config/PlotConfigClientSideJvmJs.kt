/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.PlotConfig
import jetbrains.datalore.plot.config.PlotConfigClientSide
import jetbrains.datalore.plot.config.transform.encode.DataSpecEncodeTransforms

/**
 * DataSpecEncodeTransforms are only implemented for JS/JVM
 */
object PlotConfigClientSideJvmJs {
    fun processTransform(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        val isGGBunch = PlotConfig.isGGBunchSpec(plotSpec)
        @Suppress("NAME_SHADOWING")
        var plotSpec = DataSpecEncodeTransforms.clientSideDecode(isGGBunch).apply(plotSpec)

        @Suppress("NAME_SHADOWING")
        return PlotConfigClientSide.processTransform(plotSpec)
    }
}
/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.PlotConfigClientSide

/**
 * DataSpecEncodeTransforms are only implemented for JS/JVM
 */
object PlotConfigClientSideJvmJs {
    fun processTransform(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        return PlotConfigClientSide.processTransform(plotSpec)
    }
}
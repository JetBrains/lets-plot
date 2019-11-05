/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.config.transform.encode.DataSpecEncodeTransforms

object PlotConfigServerSideJvm {
    fun processTransformWithEncoding(plotSpec: MutableMap<String, Any>): MutableMap<String, Any> {
        @Suppress("NAME_SHADOWING")
        var plotSpec = PlotConfigServerSide.processTransform(plotSpec)
        plotSpec = DataSpecEncodeTransforms.serverSideEncode(false).apply(plotSpec)
        return plotSpec
    }
}

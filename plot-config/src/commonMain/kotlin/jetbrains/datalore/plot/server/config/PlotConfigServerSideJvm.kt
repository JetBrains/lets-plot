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

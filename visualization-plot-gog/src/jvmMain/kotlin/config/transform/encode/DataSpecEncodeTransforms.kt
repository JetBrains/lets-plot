package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransform
import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransform.Builder

fun DataSpecEncodeTransforms.serverSideEncode(forRawSpec: Boolean): PlotSpecTransform {
    val builder: Builder
    if (forRawSpec) {
        builder = PlotSpecTransform.builderForRawSpec()
    } else {
        builder = PlotSpecTransform.builderForCleanSpec()
    }

    return addDataChanges(builder, ServerSideEncodeChange(), false).build()
}

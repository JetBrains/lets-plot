package jetbrains.datalore.visualization.plot

import jetbrains.datalore.visualization.plot.builder.Plot

//
// in JVM because Monolithic depends on JVM
//

object DemoAndTestJvm {
    @JvmOverloads
    fun createPlot(plotSpec: MutableMap<String, Any>, andBuildComponent: Boolean = true): Plot {
        val plot = Monolithic.createPlot(plotSpec, null)
        if (andBuildComponent) {
            val rootGroup = plot.rootGroup
        }
        return plot
    }
}
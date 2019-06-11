package jetbrains.datalore.visualization.plotDemo.model

import jetbrains.datalore.base.geometry.DoubleVector

open class PlotConfigDemoBase(plotSize: DoubleVector = DEFAULT_PLOT_SIZE) : SimpleDemoBase(plotSize) {

    companion object {
        private val DEFAULT_PLOT_SIZE = DoubleVector(400.0, 300.0)
//        private val DEFAULT_PADDING = DoubleVector(20.0, 20.0)
//        private val DEFAULT_THEME = DefaultTheme()
//
//        val EMPTY_GEOM_CONTEXT: GeomContext = EmptyGeomContext()
    }

}
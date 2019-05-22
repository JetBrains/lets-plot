package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.event3.MouseEventSource
import jetbrains.datalore.visualization.plot.base.render.Aesthetics

interface LivemapProvider {
    fun createLivemap(
            aesthetics: Aesthetics,
            dataAccess: MappedDataAccess,
            bounds: DoubleRectangle,
            eventSource: MouseEventSource,
            layers: List<LivemapLayerData>
    ): LivemapData

    class LivemapData(val canvasFigure: CanvasFigure, val targetLocator: GeomTargetLocator)
}

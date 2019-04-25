package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.plot.event3.MouseEventSource

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

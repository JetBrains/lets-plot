package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.event3.MouseEventSource
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.Geom
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapGeom
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapLayerData
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapProvider.LivemapData

internal class LivemapLayerRenderer(private val myAesthetics: Aesthetics, private val myGeom: LivemapGeom, private val myDataAccess: MappedDataAccess) : GeomLayerRenderer {
    private val myLayers = ArrayList<LivemapLayerData>()

    fun addDataLayer(geom: Geom, geomKind: GeomKind, aesthetics: Aesthetics, dataAccess: MappedDataAccess) {
        myLayers.add(LivemapLayerData(geom, geomKind, aesthetics, dataAccess))
    }

    fun createLivemapData(bounds: DoubleRectangle, eventSource: MouseEventSource): LivemapData {
        return myGeom.createCanvasFigure(myAesthetics, myDataAccess, bounds, eventSource, myLayers)
    }
}

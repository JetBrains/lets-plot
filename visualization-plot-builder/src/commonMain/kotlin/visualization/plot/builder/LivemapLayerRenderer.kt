package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.event3.MouseEventSource
import jetbrains.datalore.visualization.plot.base.render.Aesthetics
import jetbrains.datalore.visualization.plot.base.render.Geom
import jetbrains.datalore.visualization.plot.base.render.geom.LivemapGeom
import jetbrains.datalore.visualization.plot.base.render.geom.LivemapLayerData
import jetbrains.datalore.visualization.plot.base.render.geom.LivemapProvider.LivemapData

internal class LivemapLayerRenderer(private val myAesthetics: Aesthetics, private val myGeom: LivemapGeom, private val myDataAccess: MappedDataAccess) : GeomLayerRenderer {
    private val myLayers = ArrayList<LivemapLayerData>()

    fun addDataLayer(geom: Geom, geomKind: GeomKind, aesthetics: Aesthetics, dataAccess: MappedDataAccess) {
        myLayers.add(LivemapLayerData(geom, geomKind, aesthetics, dataAccess))
    }

    fun createLivemapData(bounds: DoubleRectangle, eventSource: MouseEventSource): LivemapData {
        return myGeom.createCanvasFigure(myAesthetics, myDataAccess, bounds, eventSource, myLayers)
    }
}

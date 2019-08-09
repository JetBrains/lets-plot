package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.Geom
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.geom.LivemapGeom
import jetbrains.datalore.visualization.plot.base.geom.LivemapLayerData
import jetbrains.datalore.visualization.plot.base.geom.LivemapProvider.LivemapData
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess

internal class LivemapLayerRenderer(private val myAesthetics: Aesthetics, private val myGeom: LivemapGeom, private val myDataAccess: MappedDataAccess) : GeomLayerRenderer {
    private val myLayers = ArrayList<LivemapLayerData>()

    fun addDataLayer(geom: Geom, geomKind: GeomKind, aesthetics: Aesthetics, dataAccess: MappedDataAccess) {
        myLayers.add(LivemapLayerData(geom, geomKind, aesthetics, dataAccess))
    }

    fun createLivemapData(bounds: DoubleRectangle, eventSource: MouseEventSource): LivemapData {
        return myGeom.createCanvasFigure(myAesthetics, myDataAccess, bounds, eventSource, myLayers)
    }
}

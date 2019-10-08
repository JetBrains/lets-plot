package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.Geom
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.geom.LiveMapGeom
import jetbrains.datalore.visualization.plot.base.geom.LiveMapLayerData
import jetbrains.datalore.visualization.plot.base.geom.LiveMapProvider.LiveMapData
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess

internal class LivemapLayerRenderer(private val myAesthetics: Aesthetics, private val myGeom: LiveMapGeom, private val myDataAccess: MappedDataAccess) : GeomLayerRenderer {
    private val myLayers = ArrayList<LiveMapLayerData>()

    fun addDataLayer(geom: Geom, geomKind: GeomKind, aesthetics: Aesthetics, dataAccess: MappedDataAccess) {
        myLayers.add(LiveMapLayerData(geom, geomKind, aesthetics, dataAccess))
    }

    fun createLiveMapData(bounds: DoubleRectangle): LiveMapData {
        return myGeom.createCanvasFigure(myAesthetics, myDataAccess, bounds, myLayers)
    }
}

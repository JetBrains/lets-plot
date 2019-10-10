package jetbrains.datalore.plot.builder

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.Geom
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.geom.LiveMapGeom
import jetbrains.datalore.visualization.plot.base.geom.LiveMapLayerData
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess

internal class LivemapLayerRenderer(private val myAesthetics: Aesthetics, private val myGeom: LiveMapGeom, private val myDataAccess: MappedDataAccess) :
    GeomLayerRenderer {
    private val myLayers = ArrayList<LiveMapLayerData>()

    fun addDataLayer(geom: Geom, geomKind: GeomKind, aesthetics: Aesthetics, dataAccess: MappedDataAccess) {
        myLayers.add(LiveMapLayerData(geom, geomKind, aesthetics, dataAccess))
    }

    fun createLiveMapFigure(dimension: DoubleVector): CanvasFigure {
        return myGeom.createCanvasFigure(myAesthetics, myDataAccess, dimension, myLayers)
    }
}

package jetbrains.livemap.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.config.LiveMapOptions
import jetbrains.datalore.visualization.base.canvasFigure.CanvasFigure
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.GeomKind.*
import jetbrains.datalore.visualization.plot.base.geom.LiveMapLayerData
import jetbrains.datalore.visualization.plot.base.geom.LiveMapProvider
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapCanvasFigure
import jetbrains.livemap.LiveMapFactory

object LiveMapUtil {

    fun containsLiveMap(layersByTile: List<List<GeomLayer>>): Boolean {
        return jetbrains.datalore.plot.builder.GeomLayerListUtil.containsLivemapLayer(layersByTile)
    }

    fun injectLiveMapProvider(layersByTile: List<List<GeomLayer>>, liveMapOptions: LiveMapOptions) {
        val liveMapProvider = MyLiveMapProvider(liveMapOptions)
        for (tiles in layersByTile) {
            for (layer in tiles) {
                if (layer.isLivemap) {
                    layer.setLiveMapProvider(liveMapProvider)
                }
            }
        }
    }

//    internal fun createTooltipAesSpec(geomKind: GeomKind, dataAccess: MappedDataAccess): TooltipAesSpec {
//        val aesList = ArrayList(dataAccess.getMappedAes())
//        aesList.removeAll(getHiddenAes(geomKind))
//        return TooltipAesSpec.create(aesList, emptyList<T>(), dataAccess)
//    }

    private fun getHiddenAes(geomKind: GeomKind): List<Aes<*>> {
        val hiddenAes = ArrayList<Aes<*>>()
        hiddenAes.add(Aes.MAP_ID)

        when (geomKind) {
            POINT,
            POLYGON,
            CONTOUR,
            CONTOURF,
            DENSITY2D,
            DENSITY2DF,
            PATH,
            TILE,
            V_LINE,
            H_LINE -> hiddenAes.addAll(listOf(Aes.X, Aes.Y))

            RECT -> hiddenAes.addAll(listOf(Aes.YMIN, Aes.YMAX, Aes.XMIN, Aes.XMAX))

            SEGMENT -> hiddenAes.addAll(listOf(Aes.X, Aes.Y, Aes.XEND, Aes.YEND))

            else -> {}
        }

        return hiddenAes
    }

    private class MyLiveMapProvider internal constructor(private val myLiveMapOptions: LiveMapOptions) :
        LiveMapProvider {

        override fun createLiveMap(
            aesthetics: Aesthetics,
            dataAccess: MappedDataAccess,
            dimension: DoubleVector,
            layers: List<LiveMapLayerData>
        ): CanvasFigure {

            val liveMapSpec = LiveMapSpecBuilder()
                .livemapOptions(myLiveMapOptions)
                .aesthetics(aesthetics)
                .dataAccess(dataAccess)
                .size(dimension)
                .layers(layers)
                .devParams(DevParams(myLiveMapOptions.devParams))
                .mapLocationConsumer { locationRect ->
                    //LiveMapClipboardProvider().get().copy(LiveMapLocation.getLocationString(locationRect))
                }
                .build()

            val liveMapFactory = LiveMapFactory(liveMapSpec)
            val liveMapCanvasFigure = LiveMapCanvasFigure(liveMapFactory.createLiveMap())
            liveMapCanvasFigure.setDimension(dimension)

            return liveMapCanvasFigure
        }
    }
}
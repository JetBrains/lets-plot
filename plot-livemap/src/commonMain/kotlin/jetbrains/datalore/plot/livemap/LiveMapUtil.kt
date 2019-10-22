package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomKind.*
import jetbrains.datalore.plot.base.geom.LiveMapProvider
import jetbrains.datalore.plot.base.livemap.LiveMapOptions
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.LayerRendererUtil
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapCanvasFigure
import jetbrains.livemap.LiveMapFactory

object LiveMapUtil {

    fun injectLiveMapProvider(plotTiles: List<List<GeomLayer>>, liveMapOptions: LiveMapOptions) {
        plotTiles.forEach { tileLayers ->
            tileLayers
                .firstOrNull { it.isLiveMap }
                ?.setLiveMapProvider(
                    MyLiveMapProvider(
                        tileLayers,
                        liveMapOptions
                    )
                )
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

            else -> {
            }
        }

        return hiddenAes
    }

    private class MyLiveMapProvider internal constructor(
        geomLayers: List<GeomLayer>,
        private val myLiveMapOptions: LiveMapOptions
    ) : LiveMapProvider {

        private val liveMapSpecBuilder: LiveMapSpecBuilder

        init {
            require(geomLayers.isNotEmpty())
            require(geomLayers.first().isLiveMap) { "geom_livemap have to be the very first geom after ggplot()" }

            // liveMap uses raw positions, so no mappings needed
            val newLiveMapRendererData = { layer: GeomLayer ->
                LayerRendererUtil.createLayerRendererData(
                    layer,
                    emptyMap(),
                    emptyMap()
                )
            }

            // feature geom layers
            val layers = geomLayers
                .drop(1) // skip geom_livemap
                .map(newLiveMapRendererData)
                .map {
                    with(it) {
                        LiveMapLayerData(
                            geom,
                            geomKind,
                            aesthetics,
                            dataAccess
                        )
                    }
                }

            // LiveMap geom layer
            newLiveMapRendererData(geomLayers.first()).let {
                liveMapSpecBuilder = LiveMapSpecBuilder()
                    .liveMapOptions(myLiveMapOptions)
                    .aesthetics(it.aesthetics)
                    .dataAccess(it.dataAccess)
                    .layers(layers)
                    .devParams(DevParams(myLiveMapOptions.devParams))
                    .mapLocationConsumer { _ ->
                        //LiveMapClipboardProvider().get().copy(LiveMapLocation.getLocationString(locationRect))
                    }
            }
        }

        override fun createLiveMap(dimension: DoubleVector): CanvasFigure {
            return liveMapSpecBuilder.size(dimension).build()
                .let { liveMapSpec -> LiveMapFactory(liveMapSpec).createLiveMap() }
                .let { liveMapAsync -> LiveMapCanvasFigure(liveMapAsync).apply { setDimension(dimension) } }
        }
    }
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.Services
import jetbrains.livemap.api.liveMapConfig
import jetbrains.livemap.api.projection
import jetbrains.livemap.canvascontrols.LiveMapPresenter
import jetbrains.livemap.config.LiveMapFactory
import jetbrains.livemap.core.projections.ProjectionType
import jetbrains.livemap.tiles.TileSystemProvider.RasterTileSystemProvider
import jetbrains.livemap.ui.Clipboard

abstract class DemoModelBase(private val dimension: DoubleVector) {
    fun show(canvasControl: CanvasControl, block: LiveMapBuilder.() -> Unit = {}): Registration {
        val liveMap = createLiveMapSpec()
            .apply(block)
            .run(LiveMapBuilder::build)
            .run(::LiveMapFactory)
            .run(LiveMapFactory::createLiveMap)

        return Registration.from(
            LiveMapPresenter().apply { render(canvasControl, liveMap) }
        )
    }

    internal fun basicLiveMap(block: LiveMapBuilder.() -> Unit): LiveMapBuilder {
        return liveMapConfig {
            // raster tiles without geocoding
            tileSystemProvider = RasterTileSystemProvider("https://maps.wikimedia.org/osm-intl/{z}/{x}/{y}.png")
            geocodingService = Services.bogusGeocodingService()

            // vector tiles and geocoding
            //tileSystemProvider = VectorTileSystemProvider(Services.devTileProvider())
            //geocodingService = Services.devGeocodingService()


            size = dimension
            mapLocationConsumer = { Clipboard.copy(LiveMapLocation.getLocationString(it)) }

            projection {
                kind = ProjectionType.MERCATOR
            }

//            params(
//                DevParams.DEBUG_GRID.key to true,
//                DevParams.MICRO_TASK_EXECUTOR.key to "ui_thread",
//                DevParams.PERF_STATS.key to true
//            )

        }.apply(block)
    }

    abstract fun createLiveMapSpec(): LiveMapBuilder
}

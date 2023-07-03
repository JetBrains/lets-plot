/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import org.jetbrains.letsPlot.base.intern.async.Asyncs
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.api.LiveMapBuilder
import jetbrains.livemap.api.Services
import jetbrains.livemap.api.liveMapConfig
import jetbrains.livemap.api.projection
import jetbrains.livemap.canvascontrols.LiveMapPresenter
import jetbrains.livemap.core.Clipboard
import jetbrains.livemap.core.Projections
import jetbrains.livemap.mapengine.basemap.Tilesets

abstract class DemoModelBase(private val dimension: DoubleVector) {
    fun show(canvasControl: CanvasControl, block: LiveMapBuilder.() -> Unit = {}): Registration {
        val liveMap = createLiveMapSpec()
            .apply(block)
            .run(LiveMapBuilder::build)
            .run(Asyncs::constant)

        return Registration.from(
            LiveMapPresenter().apply { render(canvasControl, liveMap) }
        )
    }

    internal fun basicLiveMap(block: LiveMapBuilder.() -> Unit): LiveMapBuilder {
        return liveMapConfig {
            //tileSystemProvider = RasterTileSystemProvider("https://a.tile.openstreetmap.org/{z}/{x}/{y}.png")
            tileSystemProvider = Tilesets.letsPlot(Services.jetbrainsTileProvider())

            geocodingService = Services.bogusGeocodingService()
            //geocodingService = Services.jetbrainsGeocodingService()
            attribution = "<a href=\"https://www.openstreetmap.org/copyright\">© OpenStreetMap contributors</a>"


            size = dimension
            mapLocationConsumer = { Clipboard.copy(LiveMapLocation.getLocationString(it)) }

            projection {
                geoProjection = Projections.mercator()
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

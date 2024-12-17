/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.livemap.demo

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasControl
import org.jetbrains.letsPlot.livemap.LiveMapLocation
import org.jetbrains.letsPlot.livemap.api.LiveMapBuilder
import org.jetbrains.letsPlot.livemap.api.Services
import org.jetbrains.letsPlot.livemap.api.liveMapConfig
import org.jetbrains.letsPlot.livemap.api.projection
import org.jetbrains.letsPlot.livemap.canvascontrols.LiveMapPresenter
import org.jetbrains.letsPlot.livemap.core.Clipboard
import org.jetbrains.letsPlot.livemap.core.Projections
import org.jetbrains.letsPlot.livemap.mapengine.basemap.Tilesets

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

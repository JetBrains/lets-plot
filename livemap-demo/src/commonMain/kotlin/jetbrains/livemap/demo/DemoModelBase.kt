/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.demo

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.CanvasControl
import jetbrains.gis.tileprotocol.TileService
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapFactory
import jetbrains.livemap.LiveMapLocation
import jetbrains.livemap.api.*
import jetbrains.livemap.canvascontrols.LiveMapPresenter
import jetbrains.livemap.projections.ProjectionType
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
            size = dimension

            tileService = internalTiles {
                theme = TileService.Theme.COLOR
                host = "tiles.datalore.io"
                port = null
            }

            geocodingService = liveMapGeocoding {
                host = "geo.datalore.io"
                port = null
            }

            interactive = true

            projection {
                kind = ProjectionType.MERCATOR
                loopX = true
                loopY = false
            }

            params(
                DevParams.DEBUG_GRID.key to true,
                DevParams.MICRO_TASK_EXECUTOR.key to "ui_thread",
                DevParams.PERF_STATS.key to true
            )

            mapLocationConsumer = { Clipboard.copy(LiveMapLocation.getLocationString(it)) }
        }
            .apply(block)
    }

    abstract fun createLiveMapSpec(): LiveMapBuilder
}

package jetbrains.livemap.demo

import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.CanvasControl
import jetbrains.datalore.visualization.plot.base.livemap.LivemapConstants
import jetbrains.livemap.DevParams
import jetbrains.livemap.LiveMapFactory
import jetbrains.livemap.LiveMapSpec
import jetbrains.livemap.api.*
import jetbrains.livemap.canvascontrols.LiveMapPresenter
import jetbrains.livemap.projections.ProjectionType

abstract class DemoModelBase(private val canvasControl: CanvasControl) {
    fun show(): Registration {
        val liveMap = LiveMapFactory(createLiveMapSpec()).createLiveMap()
        val liveMapPresenter = LiveMapPresenter()

        liveMapPresenter.render(canvasControl, liveMap)

        return Registration.from(liveMapPresenter)

    }

    internal fun basicLiveMap(block: LiveMapBuilder.() -> Unit): LiveMapSpec {
        return liveMapConfig {
            size = canvasControl.size.toDoubleVector()

            tileService = internalTiles {
                theme = LivemapConstants.Theme.COLOR
                host = "tiles.datalore.io"
                port = null
            }

            geocodingService = liveMapGeocoding {
                host = "geo.datalore.io"
                port = null
            }

            theme = LivemapConstants.Theme.COLOR
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
        }
            .apply(block)
            .build()
    }

    abstract fun createLiveMapSpec(): LiveMapSpec
}

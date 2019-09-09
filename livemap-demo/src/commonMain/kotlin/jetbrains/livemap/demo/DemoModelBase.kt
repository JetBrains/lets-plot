package jetbrains.livemap.demo

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.event.MouseEventSource
import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.observable.event.EventHandler
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
        val livemap = LiveMapFactory(createLiveMapSpec()).createLiveMap()
        val livemapPresenter = LiveMapPresenter()

        livemapPresenter.render(canvasControl, livemap)

        return Registration.from(livemapPresenter)

    }

    internal fun basicLiveMap(block: LiveMapBuilder.() -> Unit): LiveMapSpec {
        return liveMapConfig {
            size = canvasControl.size.toDoubleVector()
            mouseEventSource = mouseListener(canvasControl)

            tileService = internalTiles {
                theme = LivemapConstants.Theme.COLOR
            }

            geocodingService = dummyGeocodingService

            zoom = 1
            theme = LivemapConstants.Theme.COLOR
            interactive = true

            projection {
                kind = ProjectionType.MERCATOR
                loopX = true
                loopY = false
            }

            params(
                DevParams.DEBUG_GRID.key to true,
                DevParams.MICRO_TASK_EXECUTOR.key to "ui_thread"
            )
        }
            .apply(block)
            .build()
    }

    abstract fun createLiveMapSpec(): LiveMapSpec

}

private fun mouseListener(canvasControl: CanvasControl): MouseEventSource {
    return object : MouseEventSource {
        override fun addEventHandler(
            eventSpec: MouseEventSpec,
            eventHandler: EventHandler<MouseEvent>
        ): Registration {
            return canvasControl.addEventHandler(eventSpec, eventHandler)
        }
    }
}

/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import org.jetbrains.letsPlot.base.platf.awt.AwtContainerDisposer
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.base.values.SomeFig
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.vis.canvas.awt.AwtAnimationTimerPeer
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import java.awt.Color
import java.awt.Cursor
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JLayeredPane


class AwtLiveMapPanel(
    private val liveMapFigures: List<SomeFig>,
    private val plotOverlayComponent: JComponent,
    private val executor: (() -> Unit) -> Unit,
    private val cursorServiceConfig: CursorServiceConfig
) : JLayeredPane(), Disposable {
    private val awtContainerDisposer = AwtContainerDisposer(this)
    private val mappers: MutableList<() -> Unit> = ArrayList()
    private val registrations: MutableList<Registration> = ArrayList()

    init {
        cursorServiceConfig.defaultSetter { plotOverlayComponent.cursor = Cursor(Cursor.CROSSHAIR_CURSOR) }
        cursorServiceConfig.pointerSetter { plotOverlayComponent.cursor = Cursor(Cursor.HAND_CURSOR) }

//        background = Color.WHITE
        isOpaque = false
        preferredSize = plotOverlayComponent.preferredSize

        // layout
        plotOverlayComponent.bounds = Rectangle(
            preferredSize.width,
            preferredSize.height
        )

        add(plotOverlayComponent)

//        plotContainer.liveMapFigures
        liveMapFigures
            .map { it as CanvasFigure }
            .forEach { liveMapFigures ->
                val liveMapBounds = liveMapFigures.bounds().get()
                val livemapCanvasControl = AwtCanvasControl(
                    liveMapBounds.dimension,
                    AwtEventPeer(plotOverlayComponent, liveMapBounds),
                    AwtAnimationTimerPeer(executor).also { registrations.add(Registration.from(it)) }
                )
                mappers.add {
                    liveMapFigures.mapToCanvas(livemapCanvasControl).also(registrations::add)
                }

                add(
                    object : JComponent(), Disposable {
                        override fun dispose() {}
                    }
                        .apply {
                            background = Color.WHITE
                            bounds = liveMapBounds.run { Rectangle(origin.x, origin.y, dimension.x, dimension.y) }
                            add(livemapCanvasControl.component())
                        }
                )
            }

        this.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                // Used just once.
                this@AwtLiveMapPanel.removeComponentListener(this)
                executor {
                    mappers.forEach { it() }
                    mappers.clear()
                }
            }
        })
    }

    override fun dispose() {
        awtContainerDisposer.dispose()
        registrations.forEach(Disposable::dispose)
//        plotContainer.dispose()
        cursorServiceConfig.defaultSetter { }
        cursorServiceConfig.pointerSetter { }
    }
}
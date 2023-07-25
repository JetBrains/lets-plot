/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.util.AwtContainerDisposer
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.commons.values.SomeFig
import org.jetbrains.letsPlot.core.plot.livemap.CursorServiceConfig
import org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer
import org.jetbrains.letsPlot.awt.canvas.AwtCanvasControl
import org.jetbrains.letsPlot.awt.canvas.AwtEventPeer
import org.jetbrains.letsPlot.core.canvasFigure.CanvasFigure
import java.awt.Color
import java.awt.Cursor
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JLayeredPane


internal class AwtLiveMapPanel(
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
                    org.jetbrains.letsPlot.awt.canvas.AwtAnimationTimerPeer(executor).also { registrations.add(Registration.from(it)) }
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
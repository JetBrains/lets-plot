/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.event.MouseEventSpec
import jetbrains.datalore.base.event.awt.AwtEventUtil
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.plot.builder.PlotContainer
import jetbrains.datalore.plot.livemap.CursorServiceConfig
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.awt.AwtTimerPeer
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import java.awt.Color
import java.awt.Cursor
import java.awt.Rectangle
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLayeredPane
import javax.swing.JPanel


class AwtLiveMapPanel(
    private val plotContainer: PlotContainer,
    private val plotComponent: JComponent,
    private val executor: (() -> Unit) -> Unit,
    private val cursorServiceConfig: CursorServiceConfig
) : JLayeredPane(), Disposable {
    private val awtContainerDisposer = AwtContainerDisposer(this)
    private val mappers: MutableList<() -> Unit> = ArrayList()
    private val registrations: MutableList<Registration> = ArrayList()
    private val mouseMoutionListener = object : MouseAdapter() {
        override fun mouseDragged(e: MouseEvent) {
            super.mouseDragged(e)
            executor {
                plotContainer.mouseEventPeer.dispatch(MouseEventSpec.MOUSE_MOVED, AwtEventUtil.translate(e))
            }
        }
    }

    init {
        // Move tooltip when map moved
        plotComponent.addMouseMotionListener(mouseMoutionListener)

        cursorServiceConfig.defaultSetter { plotComponent.cursor = Cursor.getDefaultCursor() }
        cursorServiceConfig.pointerSetter { plotComponent.cursor = Cursor(Cursor.HAND_CURSOR) }

        background = Color.WHITE
        preferredSize = plotComponent.preferredSize

        // layout
        plotComponent.bounds = Rectangle(
            0, 0,
            preferredSize.width,
            preferredSize.height
        )

        this.add(plotComponent)

        val timer = AwtTimerPeer(executor)

        plotContainer.liveMapFigures
            .map { it as CanvasFigure }
            .forEach { canvasFigure ->
                val canvasBounds = canvasFigure.bounds().get()

                val layerPanel = object : JLayeredPane(), Disposable {
                    override fun dispose() {
                        timer.dispose()
                    }
                }.apply {
                    background = Color.WHITE
                    bounds = Rectangle(
                        canvasBounds.origin.x,
                        canvasBounds.origin.y,
                        canvasBounds.dimension.x,
                        canvasBounds.dimension.y
                    )
                }
                this.add(layerPanel)

                AwtCanvasControl(
                    layerPanel,
                    canvasBounds.dimension,
                    1.0,
                    AwtEventPeer(plotComponent, canvasBounds),
                    timer
                ).let {
                    mappers.add {
                        registrations.add(canvasFigure.mapToCanvas(it))
                    }
                }
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
        plotComponent.removeMouseMotionListener(mouseMoutionListener)
        plotContainer.clearContent()
        cursorServiceConfig.defaultSetter { }
        cursorServiceConfig.pointerSetter { }
    }
}
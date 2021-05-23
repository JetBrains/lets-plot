/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.vis.canvas.awt.AwtCanvasControl
import jetbrains.datalore.vis.canvas.awt.AwtEventPeer
import jetbrains.datalore.vis.canvas.awt.AwtRepaintTimer
import jetbrains.datalore.vis.canvasFigure.CanvasFigure
import java.awt.*
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent
import javax.swing.JPanel

class AwtLiveMapPanel(
    liveMapFigures: List<CanvasFigure>,
    plotComponent: JComponent
) : DisposableJPanel(null) {
    private val mappers: MutableList<() -> Unit> = ArrayList()
    private val livemaps: MutableList<Registration> = ArrayList()

    init {
        background = Color.WHITE
        preferredSize = plotComponent.preferredSize

        // layout
        plotComponent.bounds = Rectangle(
            0, 0,
            preferredSize.width,
            preferredSize.height
        )

        this.add(plotComponent)

        // ToDo: use timer only then neeeded (zooming, dragging)
        val timer = AwtRepaintTimer(this::repaint)

        liveMapFigures.forEach { canvasFigure ->
            val canvasBounds = canvasFigure.bounds().get()

            val layerPanel = object : JPanel(), Disposable {
                override fun dispose() {
                    timer.dispose()
                    livemaps.forEach(Disposable::dispose)
                }
            }.apply {
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
                    livemaps.add(canvasFigure.mapToCanvas(it))
                }
            }
        }


        this.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                // Used just once.
                this@AwtLiveMapPanel.removeComponentListener(this)
                EventQueue.invokeLater {
                    mappers.forEach { it() }
                    mappers.clear()
                }
            }
        })
    }
}
/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.registration.Disposable
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
    plotComponent: JComponent,
    size: DoubleVector
) : DisposableJPanel() {
    private val mappers: MutableList<() -> Unit> = ArrayList()

    init {
        val plotBounds = Rectangle(0, 0, size.x.toInt(), size.y.toInt())
        plotComponent.bounds = plotBounds

        this.add(plotComponent)

        val timer = AwtRepaintTimer(this::repaint)

        liveMapFigures.forEach { canvasFigure ->
            val canvasBounds = canvasFigure.bounds().get()

            val layerPanel = object : JPanel(), Disposable {
                override fun dispose() {
                    // ToDo: dispose canvas stuff?
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
                    canvasFigure.mapToCanvas(it)
                }
            }
        }

        // Fixed panel minimum size for scroll pane
        val plotDimensions = Dimension(
            plotBounds.width,
            plotBounds.height
        )

        this.preferredSize = plotDimensions
        this.minimumSize = plotDimensions
        this.maximumSize = plotDimensions

        this.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                this@AwtLiveMapPanel.removeComponentListener(this)
                EventQueue.invokeLater {
                    mappers.forEach { it() }
                    mappers.clear()
                }
            }
        })
    }

    override fun paint(g: Graphics?) {
        // Fix background color
        g?.color = Color.WHITE
        g?.fillRect(0, 0, this.width, this.height)
        super.paint(g)
    }
}
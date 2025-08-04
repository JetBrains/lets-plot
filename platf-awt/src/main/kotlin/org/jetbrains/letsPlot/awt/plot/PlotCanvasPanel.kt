package org.jetbrains.letsPlot.awt.plot

import org.jetbrains.letsPlot.awt.canvas.AwtCanvasPeer
import org.jetbrains.letsPlot.awt.canvas.AwtContext2d
import org.jetbrains.letsPlot.awt.canvas.AwtMouseEventMapper
import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.event.MouseEventSource
import org.jetbrains.letsPlot.commons.event.MouseEventSpec
import org.jetbrains.letsPlot.commons.intern.observable.event.EventHandler
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.core.canvas.CanvasPeer
import org.jetbrains.letsPlot.raster.view.PlotCanvasFigure
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JComponent

class PlotCanvasPanel(
    fig: PlotCanvasFigure? = null
) : JComponent() {
    private val canvasPeer: CanvasPeer = AwtCanvasPeer()
    private var figureRegistration: Registration = Registration.EMPTY
    private val mouseEventSource: MouseEventSource = AwtMouseEventMapper(this)

    var figure: PlotCanvasFigure? = null
        set(canvasFigure) {
            if (field == canvasFigure) {
                return
            }

            figureRegistration.remove()
            if (canvasFigure != null) {
                figureRegistration = CompositeRegistration(
                    canvasFigure.mapToCanvas(canvasPeer),
                    canvasFigure.onRepaintRequest(::repaint),
                )
            }
            field = canvasFigure
        }

    init {
        this.figure = fig

        val sizeListener = object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                val size = e?.component?.size ?: return
                figure?.resize(size.width, size.height)
            }
        }

        addComponentListener(sizeListener)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        if (figure != null) {
            figure!!.draw(AwtContext2d(g2d))
        }
    }

    fun addEventHandler(eventSpec: MouseEventSpec, eventHandler: EventHandler<MouseEvent>): Registration {
        return mouseEventSource.addEventHandler(eventSpec, eventHandler)
    }
}

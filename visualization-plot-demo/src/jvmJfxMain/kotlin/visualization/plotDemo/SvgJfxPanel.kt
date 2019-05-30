package jetbrains.datalore.visualization.plotDemo

import javafx.scene.Parent
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.visualization.base.canvas.javaFx.JavafxCanvasControl
import jetbrains.datalore.visualization.base.svg.SvgConstants
import jetbrains.datalore.visualization.base.svg.SvgElementListener
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent
import jetbrains.datalore.visualization.base.svgToCanvas.SvgCanvasRenderer
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec.MOUSE_LEFT as CANVAS_MOUSE_LEFT
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec.MOUSE_MOVED as CANVAS_MOUSE_MOVED

class SvgJfxPanel(private val svg: SvgSvgElement) : SwingJfxPanel(), Disposable {
    private val registration = CompositeRegistration()

    init {
        border = BorderFactory.createLineBorder(Color.BLUE, 3)
        
        svg.addListener(object : SvgElementListener {
            override fun onAttrSet(event: SvgAttributeEvent<*>) {
                if (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) || SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true)) {
//                    this@SvgJfxPanel.invalidate()
//                    this@SvgJfxPanel.repaint()
                }
            }
        })
    }

    override fun createSceneParent(): Parent {

        val canvasSize = getSvgIntSize()
        val canvasControl = JavafxCanvasControl(canvasSize, 1.0)

        reg(SvgCanvasRenderer(svg, canvasControl))
//        reg(createEventMapper(CANVAS_MOUSE_LEFT, MouseEventSpec.MOUSE_LEFT))
//        reg(createEventMapper(CANVAS_MOUSE_MOVED, MouseEventSpec.MOUSE_MOVED))

        return canvasControl.javafxRoot
    }

    private fun reg(disposable: Disposable) {
        registration.add(Registration.from(disposable))
    }

    private fun reg(reg: Registration) {
        registration.add(reg)
    }

    override fun dispose() {
        registration.dispose()
    }

    override fun getPreferredSize(): Dimension {
        val size = getSvgIntSize()
        return Dimension(size.x, size.y)
    }

    private fun getSvgIntSize(): Vector {
        return Vector(svg.width().get()!!.toInt(), svg.height().get()!!.toInt())
    }
}

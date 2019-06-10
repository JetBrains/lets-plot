package jetbrains.datalore.visualization.base.swing

import javafx.scene.Parent
import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.visualization.base.svg.SvgConstants
import jetbrains.datalore.visualization.base.svg.SvgElementListener
import jetbrains.datalore.visualization.base.svg.SvgNodeContainer
import jetbrains.datalore.visualization.base.svg.SvgSvgElement
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent
import jetbrains.datalore.visualization.base.svgMapper.jfx.SvgAwtPeer
import jetbrains.datalore.visualization.base.svgMapper.jfx.SvgSvgElementMapper
import java.awt.Color
import java.awt.Dimension
import javax.swing.BorderFactory
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec.MOUSE_LEFT as CANVAS_MOUSE_LEFT
import jetbrains.datalore.visualization.base.canvas.CanvasControl.EventSpec.MOUSE_MOVED as CANVAS_MOUSE_MOVED

class SvgMapperJfxPanel(private val svg: SvgSvgElement,
                        stylesheets: List<String>) : AbstractJfxPanel(stylesheets) {

    private lateinit var mySceneRoot: Parent

    init {
        border = BorderFactory.createLineBorder(Color.BLUE, 3)

        runOnFxThread {
            Preconditions.checkArgument(!svg.isAttached(), "SvgSvgElement must be unattached")
            SvgNodeContainer(svg)  // attach root
            val rootMapper = SvgSvgElementMapper(svg, SvgAwtPeer())
            rootMapper.attachRoot(MappingContext())
            mySceneRoot = rootMapper.target
        }

        svg.addListener(object : SvgElementListener {
            override fun onAttrSet(event: SvgAttributeEvent<*>) {
                if (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) || SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true)) {
//                    this@SvgJfxPanel.invalidate()
//                    this@SvgJfxPanel.repaint()
                    runOnFxThread { revalidateScene() }
                }
            }
        })
    }

    override fun createSceneParent(): Parent {
//        val canvasSize = getSvgIntSize()
//        val canvasControl = JavafxCanvasControl(canvasSize, 1.0)
//
//        regFx(SvgCanvasRenderer(svg, canvasControl))
//        return canvasControl.javafxRoot
        return mySceneRoot
    }

    override fun getPreferredSize(): Dimension {
        val size = getSvgIntSize()
        return Dimension(size.x, size.y)
    }

    private fun getSvgIntSize(): Vector {
        return Vector(svg.width().get()!!.toInt(), svg.height().get()!!.toInt())
    }
}

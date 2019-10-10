package jetbrains.datalore.vis.demoUtils.jfx

import javafx.scene.Parent
import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.mapper.core.MappingContext
import jetbrains.datalore.vis.svg.SvgConstants
import jetbrains.datalore.vis.svg.SvgElementListener
import jetbrains.datalore.vis.svg.SvgNodeContainer
import jetbrains.datalore.vis.svg.SvgSvgElement
import jetbrains.datalore.vis.svg.event.SvgAttributeEvent
import jetbrains.datalore.vis.svgMapper.jfx.SvgJfxPeer
import jetbrains.datalore.vis.svgMapper.jfx.SvgSvgElementMapper
import java.awt.Dimension

class SceneMapperJfxPanel(
    private val svg: SvgSvgElement,
    stylesheets: List<String>
) : AbstractJfxPanel(stylesheets) {

    private lateinit var mySceneRoot: Parent

    init {
        runOnFxThread {
            //            border = BorderFactory.createLineBorder(Color.BLUE, 3)  //- not working
//            foreground = Color.BLUE

            Preconditions.checkArgument(!svg.isAttached(), "SvgSvgElement must be unattached")
            SvgNodeContainer(svg)  // attach root
            val rootMapper = SvgSvgElementMapper(svg, SvgJfxPeer())
            rootMapper.attachRoot(MappingContext())
            mySceneRoot = rootMapper.target
        }

        svg.addListener(object : SvgElementListener {
            override fun onAttrSet(event: SvgAttributeEvent<*>) {
                if (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) ||
                    SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true)
                ) {
                    runOnFxThread { revalidateScene() }
                }
            }
        })
    }

    override fun createSceneParent(): Parent {
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

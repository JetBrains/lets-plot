/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import javafx.scene.Parent
import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.registration.Disposable
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
) : AbstractJfxPanel(stylesheets), Disposable {

    private var myIsDisposed: Boolean = false

    init {
        runOnFxThread {
            //            border = BorderFactory.createLineBorder(Color.BLUE, 3)  //- not working
            //            foreground = Color.BLUE

            Preconditions.checkArgument(!svg.isAttached(), "SvgSvgElement must be unattached")
            SvgNodeContainer(svg)  // attach root

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
    }

    override fun createSceneParent(): Parent {
        val rootMapper = SvgSvgElementMapper(svg, SvgJfxPeer())
        rootMapper.attachRoot(MappingContext())
        return rootMapper.target
    }

    override fun getPreferredSize(): Dimension {
        val size = getSvgIntSize()
        return Dimension(size.x, size.y)
    }

    private fun getSvgIntSize(): Vector {
        return Vector(svg.width().get()!!.toInt(), svg.height().get()!!.toInt())
    }

    override fun dispose() {
        require(!myIsDisposed) { "Alreadey disposed." }
        // Need to dispose something?
    }
}

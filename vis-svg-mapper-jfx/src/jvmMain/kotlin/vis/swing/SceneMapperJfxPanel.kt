/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.swing

import javafx.scene.Parent
import jetbrains.datalore.base.awt.AwtContainerDisposer
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.registration.CompositeRegistration
import jetbrains.datalore.base.registration.Disposable
import jetbrains.datalore.base.registration.DisposableRegistration
import jetbrains.datalore.base.registration.DisposingHub
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import jetbrains.datalore.vis.svgMapper.jfx.SvgJfxPeer
import jetbrains.datalore.vis.svgMapper.jfx.SvgSvgElementMapper
import java.awt.Dimension

class SceneMapperJfxPanel(
    private val svg: SvgSvgElement,
    stylesheets: List<String>
) : AbstractJfxPanel(stylesheets), Disposable, DisposingHub {

    private val nodeContainer = SvgNodeContainer(svg)  // attach root
    private var registrations = CompositeRegistration()

    init {
        runOnFxThread {
            //            border = BorderFactory.createLineBorder(Color.BLUE, 3)  //- not working
            //            foreground = Color.BLUE

            registrations.add(
                svg.addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
                        if (SvgConstants.HEIGHT.equals(event.attrSpec.name, ignoreCase = true) ||
                            SvgConstants.WIDTH.equals(event.attrSpec.name, ignoreCase = true)
                        ) {
                            runOnFxThread { revalidateScene() }
                        }
                    }
                })
            )
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

    override fun registerDisposable(disposable: Disposable) {
        registrations.add(DisposableRegistration(disposable))
    }

    override fun dispose() {
        registrations.dispose()

        AwtContainerDisposer(this).dispose()

        // Detach svg root.
        nodeContainer.root().set(SvgSvgElement())
    }
}

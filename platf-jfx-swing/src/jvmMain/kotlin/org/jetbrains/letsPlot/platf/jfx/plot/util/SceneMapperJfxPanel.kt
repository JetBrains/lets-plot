/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.plot.util

import javafx.scene.Parent
import org.jetbrains.letsPlot.platf.awt.util.AwtContainerDisposer
import jetbrains.datalore.base.geometry.Vector
import org.jetbrains.letsPlot.commons.registration.CompositeRegistration
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.commons.registration.DisposableRegistration
import org.jetbrains.letsPlot.commons.registration.DisposingHub
import org.jetbrains.letsPlot.datamodel.mapping.framework.MappingContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNodeContainer
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.SvgJfxPeer
import org.jetbrains.letsPlot.platf.jfx.mapping.svg.SvgSvgElementMapper
import org.jetbrains.letsPlot.platf.jfx.util.runOnFxThread
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

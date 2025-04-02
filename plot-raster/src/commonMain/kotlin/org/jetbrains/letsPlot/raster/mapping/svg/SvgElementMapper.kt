/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.framework.Mapper
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizer
import org.jetbrains.letsPlot.datamodel.mapping.framework.SynchronizerContext
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.raster.shape.Element

internal open class SvgElementMapper<SourceT : SvgElement, TargetT : Element>(
    source: SourceT,
    target: TargetT,
    peer: SvgCanvasPeer
) : SvgNodeMapper<SourceT, TargetT>(source, target, peer) {

    private var myHandlerRegs: MutableMap<SvgEventSpec, Registration>? = null

    open fun setTargetAttribute(name: String, value: Any?) {
        SvgUtils.setAttribute(target, name, value)
    }

    open fun applyStyle() {}

    override fun registerSynchronizers(conf: Mapper.SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(object : Synchronizer {
            private var myReg: Registration? = null

            override fun attach(ctx: SynchronizerContext) {
                applyStyle()

                myReg = source.addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
                        setTargetAttribute(event.attrSpec.name, event.newValue)
                    }
                })

                for (key in source.attributeKeys) {
                    val name = key.name
                    val value = source.getAttribute(name).get()
                    setTargetAttribute(name, value)
                }
            }

            override fun detach() {
                myReg!!.remove()
            }
        })
    }
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.batik.mapping.svg

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizer
import org.jetbrains.letsPlot.datamodel.mapping.framework.SynchronizerContext
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.apache.batik.anim.dom.SVGOMElement
import org.apache.batik.dom.AbstractDocument
import org.apache.batik.dom.events.DOMMouseEvent
import org.apache.batik.util.SVGConstants
import org.w3c.dom.events.EventListener
import java.util.*

internal class SvgElementMapper<SourceT : SvgElement, TargetT : SVGOMElement>(
    source: SourceT,
    target: TargetT,
    doc: AbstractDocument,
    peer: SvgBatikPeer
) : SvgNodeMapper<SourceT, TargetT>(source, target, doc, peer) {

    private var myHandlerRegs: MutableMap<SvgEventSpec, Registration>? = null

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(object : Synchronizer {
            private var myReg: Registration? = null

            override fun attach(ctx: SynchronizerContext) {
                myReg = source.addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
                        if (event.newValue == null) {
                            target.removeAttribute(event.attrSpec.name)
                        }
                        target.setAttribute(event.attrSpec.name, event.newValue.toString())
                    }

                })

                for (key in source.attributeKeys) {
                    val name = key.name
                    val value = source.getAttribute(name).get().toString()
                    if (key.hasNamespace()) {
                        target.setAttributeNS(key.namespaceUri, key.name, value)
                    } else {
                        target.setAttribute(name, value)
                    }
                }
            }

            override fun detach() {
                myReg!!.remove()
            }
        })

        conf.add(
            Synchronizers.forPropsOneWay(
                source.handlersSet(),
                object : WritableProperty<Set<SvgEventSpec>?> {
                    override fun set(value: Set<SvgEventSpec>?) {
                        if (myHandlerRegs == null) {
                            myHandlerRegs = EnumMap(SvgEventSpec::class.java)
                        }

                        for (spec in SvgEventSpec.values()) {
                            if (!value!!.contains(spec) && myHandlerRegs!!.containsKey(spec)) {
                                myHandlerRegs!!.remove(spec)!!.remove()
                            }
                            if (!value.contains(spec) || myHandlerRegs!!.containsKey(spec)) continue

                            when (spec) {
                                SvgEventSpec.MOUSE_CLICKED -> addMouseHandler(spec, SVGConstants.SVG_CLICK_EVENT_TYPE)
                                SvgEventSpec.MOUSE_PRESSED -> addMouseHandler(
                                    spec,
                                    SVGConstants.SVG_MOUSEDOWN_EVENT_TYPE
                                )
                                SvgEventSpec.MOUSE_RELEASED -> addMouseHandler(
                                    spec,
                                    SVGConstants.SVG_MOUSEUP_EVENT_TYPE
                                )
                                SvgEventSpec.MOUSE_OVER -> addMouseHandler(spec, SVGConstants.SVG_MOUSEOVER_EVENT_TYPE)
                                SvgEventSpec.MOUSE_MOVE -> addMouseHandler(spec, SVGConstants.SVG_MOUSEMOVE_EVENT_TYPE)
                                SvgEventSpec.MOUSE_OUT -> addMouseHandler(spec, SVGConstants.SVG_MOUSEOUT_EVENT_TYPE)
                                else -> {
                                }
                            }
                        }

                        if (myHandlerRegs!!.isEmpty()) {
                            myHandlerRegs = null
                        }
                    }
                })
        )
    }

    private fun addMouseHandler(spec: SvgEventSpec, eventType: String) {
        val listener = EventListener { evt ->
            evt.stopPropagation()
            val e = evt as DOMMouseEvent
            source.dispatch(
                spec, MouseEvent(
                    e.clientX, e.clientY,
                    Utils.getButton(e),
                    Utils.getModifiers(e)
                )
            )
        }

        target.addEventListener(eventType, listener, false)
        myHandlerRegs!![spec] = object : Registration() {
            override fun doRemove() {
                target.removeEventListener(eventType, listener, false)
            }
        }
    }
}

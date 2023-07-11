/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg

import org.jetbrains.letsPlot.commons.intern.function.Function
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.registration.Registration
import org.jetbrains.letsPlot.platf.w3c.dom.events.DomEventType
import org.jetbrains.letsPlot.base.platf.dom.DomEventUtil
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizer
import org.jetbrains.letsPlot.datamodel.mapping.framework.SynchronizerContext
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.on
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGElement

internal class SvgElementMapper<SourceT : SvgElement, TargetT : SVGElement>(
    source: SourceT,
    target: TargetT,
    private val myPeer: SvgDomPeer
) :
    SvgNodeMapper<SourceT, TargetT>(source, target, myPeer) {
    private var myHandlersRegs: MutableMap<SvgEventSpec, Registration>? = null

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
                        target.setAttributeNS(key.namespaceUri, name, value)
                    } else {
                        target.setAttribute(name, value)
                    }
                }
            }

            override fun detach() {
                myReg!!.remove()
            }
        })

        conf.add(Synchronizers.forPropsOneWay(source.handlersSet(), object : WritableProperty<Set<SvgEventSpec>?> {
            override fun set(value: Set<SvgEventSpec>?) {
                if (myHandlersRegs == null) {
                    myHandlersRegs = mutableMapOf()
                }

                for (spec in SvgEventSpec.values()) {
                    if (!value!!.contains(spec) && myHandlersRegs!!.containsKey(spec)) {
                        myHandlersRegs!!.remove(spec)!!.dispose()
                    }
                    if (!value.contains(spec) || myHandlersRegs!!.containsKey(spec)) continue

                    val event: DomEventType<MouseEvent> = when (spec) {
                        SvgEventSpec.MOUSE_CLICKED -> DomEventType.CLICK
                        SvgEventSpec.MOUSE_PRESSED -> DomEventType.MOUSE_DOWN
                        SvgEventSpec.MOUSE_RELEASED -> DomEventType.MOUSE_UP
                        SvgEventSpec.MOUSE_OVER -> DomEventType.MOUSE_OVER
                        SvgEventSpec.MOUSE_MOVE -> DomEventType.MOUSE_MOVE
                        SvgEventSpec.MOUSE_OUT -> DomEventType.MOUSE_OUT
                        else -> throw IllegalStateException()
                    }

                    myHandlersRegs!![spec] = target.on(event, object : Function<Event, Boolean> {
                        override fun apply(value: Event): Boolean {
                            if (value is MouseEvent) {
                                val mouseEvent = createMouseEvent(value)
                                source.dispatch(spec, mouseEvent)
                                return true
                            }
                            return false
                        }
                    })
                }
            }
        }))
    }

    override fun onDetach() {
        super.onDetach()
        if (myHandlersRegs != null) {
            for (registration in myHandlersRegs!!.values) {
                registration.dispose()
            }
            myHandlersRegs!!.clear()
        }
    }

    private fun createMouseEvent(evt: MouseEvent): org.jetbrains.letsPlot.commons.event.MouseEvent {
        evt.stopPropagation()
        val coords = myPeer.inverseScreenTransform(source, DoubleVector(evt.clientX.toDouble(), evt.clientY.toDouble()))
        return org.jetbrains.letsPlot.commons.event.MouseEvent(
            coords.x.toInt(),
            coords.y.toInt(),
            DomEventUtil.getButton(evt),
            DomEventUtil.getModifiers(evt)
        )
    }
}
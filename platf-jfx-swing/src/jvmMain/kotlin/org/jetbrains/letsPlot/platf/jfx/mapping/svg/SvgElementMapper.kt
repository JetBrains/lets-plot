/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.jfx.mapping.svg

import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizer
import org.jetbrains.letsPlot.datamodel.mapping.framework.SynchronizerContext
import org.jetbrains.letsPlot.datamodel.mapping.framework.Synchronizers
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import java.util.*
import javafx.scene.input.MouseEvent as MouseEventFx

open class SvgElementMapper<SourceT : SvgElement, TargetT : Node>(
    source: SourceT,
    target: TargetT,
    peer: SvgJfxPeer
) : SvgNodeMapper<SourceT, TargetT>(source, target, peer) {

    private var myHandlerRegs: MutableMap<SvgEventSpec, Registration>? = null


    open fun setTargetAttribute(name: String, value: Any?) {
        Utils.setAttribute(target, name, value)
    }

    open fun applyStyle() {}

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
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
                                SvgEventSpec.MOUSE_CLICKED -> addMouseHandler(spec, MouseEventFx.MOUSE_CLICKED)
                                SvgEventSpec.MOUSE_PRESSED -> addMouseHandler(spec, MouseEventFx.MOUSE_PRESSED)
                                SvgEventSpec.MOUSE_RELEASED -> addMouseHandler(spec, MouseEventFx.MOUSE_RELEASED)
                                SvgEventSpec.MOUSE_OVER -> addMouseHandler(spec, MouseEventFx.MOUSE_ENTERED)
                                SvgEventSpec.MOUSE_MOVE -> addMouseHandler(spec, MouseEventFx.MOUSE_MOVED)
                                SvgEventSpec.MOUSE_OUT -> addMouseHandler(spec, MouseEventFx.MOUSE_EXITED)
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

    private fun addMouseHandler(spec: SvgEventSpec, eventType: EventType<MouseEventFx>) {
        val listener = EventHandler<Event> { evt ->
            evt.consume()
            val e = evt as MouseEventFx
            source.dispatch(
                spec,
                MouseEvent(e.sceneX.toInt(), e.sceneY.toInt(), Utils.getButton(e), Utils.getModifiers(e))
            )
        }

        target.addEventFilter(eventType, listener)
        myHandlerRegs!![spec] = object : Registration() {
            override fun doRemove() {
                target.removeEventFilter(eventType, listener)
            }
        }
    }
}

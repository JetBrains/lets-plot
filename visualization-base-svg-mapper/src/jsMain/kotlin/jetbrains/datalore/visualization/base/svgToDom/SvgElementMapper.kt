package jetbrains.datalore.visualization.base.svgToDom

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.core.Synchronizer
import jetbrains.datalore.mapper.core.SynchronizerContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgElementListener
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomEventType
import jetbrains.datalore.visualization.base.svgToDom.domUtil.DomEventUtil
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.svg.SVGElement
import visualization.base.svgToDom.domExtensions.on

internal class SvgElementMapper<SourceT : SvgElement, TargetT : SVGElement>(source: SourceT, target: TargetT, private val myPeer: SvgDomPeer) :
        SvgNodeMapper<SourceT, TargetT>(source, target, myPeer) {
    private var myHandlersRegs: MutableMap<SvgEventSpec, Registration>? = null

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(object: Synchronizer {
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

                    val event: DomEventType<MouseEvent> = when(spec) {
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
            for  (registration in myHandlersRegs!!.values) {
                registration.dispose()
            }
            myHandlersRegs!!.clear()
        }
    }

    private fun createMouseEvent(evt: MouseEvent): jetbrains.datalore.base.event.MouseEvent {
        evt.stopPropagation()
        val coords = myPeer.inverseScreenTransform(source, DoubleVector(evt.clientX.toDouble(), evt.clientY.toDouble()))
        return jetbrains.datalore.base.event.MouseEvent(
                coords.x.toInt(),
                coords.y.toInt(),
                DomEventUtil.getButton(evt),
                DomEventUtil.getModifiers(evt)
        )
    }
}
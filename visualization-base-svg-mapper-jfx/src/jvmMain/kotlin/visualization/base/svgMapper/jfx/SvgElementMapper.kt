package jetbrains.datalore.visualization.base.svgMapper.jfx

import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Node
import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.observable.property.WritableProperty
import jetbrains.datalore.base.registration.Registration
import jetbrains.datalore.mapper.core.Synchronizer
import jetbrains.datalore.mapper.core.SynchronizerContext
import jetbrains.datalore.mapper.core.Synchronizers
import jetbrains.datalore.visualization.base.svg.SvgElement
import jetbrains.datalore.visualization.base.svg.SvgElementListener
import jetbrains.datalore.visualization.base.svg.event.SvgAttributeEvent
import jetbrains.datalore.visualization.base.svg.event.SvgEventSpec
import jetbrains.datalore.visualization.base.svgMapper.jfx.attr.SvgAttrMapping
import java.util.*

import javafx.scene.input.MouseEvent as MouseEventFx

internal open class SvgElementMapper<SourceT : SvgElement, TargetT : Node>(
        source: SourceT,
        target: TargetT,
        peer: SvgAwtPeer) : SvgNodeMapper<SourceT, TargetT>(source, target, peer) {

    private var _svgAttrMapping: SvgAttrMapping<TargetT>? = null
    private var myHandlerRegs: MutableMap<SvgEventSpec, Registration>? = null


    private val svgAttrMapping: SvgAttrMapping<TargetT>
        get() {
            if (_svgAttrMapping == null) {
                _svgAttrMapping = createSvgAttrMapping(source, target)
            }
            return _svgAttrMapping!!
        }

    override fun registerSynchronizers(conf: SynchronizersConfiguration) {
        super.registerSynchronizers(conf)

        conf.add(object : Synchronizer {
            private var myReg: Registration? = null

            override fun attach(ctx: SynchronizerContext) {
                myReg = source.addListener(object : SvgElementListener {
                    override fun onAttrSet(event: SvgAttributeEvent<*>) {
//                        if (event.newValue == null) {
//                            target.removeAttribute(event.attrSpec.name)
//                        }
                        svgAttrMapping.setAttribute(event.attrSpec.name, event.newValue)
                    }

                })

                for (key in source.attributeKeys) {
                    val name = key.name
                    val value = source.getAttribute(name).get()
                    svgAttrMapping.setAttribute(name, value)
                }
            }

            override fun detach() {
                myReg!!.remove()
            }
        })

        conf.add(Synchronizers.forPropsOneWay(
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
                }))
    }

    private fun addMouseHandler(spec: SvgEventSpec, eventType: EventType<MouseEventFx>) {
//        val listener = EventListener { evt ->
//            evt.stopPropagation()
//            val e = evt as DOMMouseEvent
//            source.dispatch(spec, MouseEvent(e.clientX, e.clientY, Utils.getButton(e), Utils.getModifiers(e)))
//        }
        val listener = EventHandler<Event> { evt ->
            evt.consume()
            val e = evt as MouseEventFx
            source.dispatch(spec, MouseEvent(e.sceneX.toInt(), e.sceneY.toInt(), Utils.getButton(e), Utils.getModifiers(e)))
        }

//        target.addEventListener(eventType, listener, false)
        target.addEventFilter(eventType, listener)
        myHandlerRegs!![spec] = object : Registration() {
            override fun doRemove() {
//                target.removeEventListener(eventType, listener, false)
                target.removeEventFilter(eventType, listener)
            }
        }
    }
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.mapping.svg.domUtil

import jetbrains.datalore.base.function.Supplier
import jetbrains.datalore.base.function.Value
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.event.ListenerCaller
import jetbrains.datalore.base.observable.event.Listeners
import jetbrains.datalore.base.observable.property.*
import jetbrains.datalore.base.registration.Registration
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimNode
import org.jetbrains.letsPlot.datamodel.svg.dom.XmlNamespace.SVG_NAMESPACE_URI
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.css.CssDisplay
import org.w3c.dom.*
import org.w3c.dom.svg.SVGElement
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.addClass
import kotlinx.dom.removeClass
import org.jetbrains.letsPlot.datamodel.svg.dom.*
import org.jetbrains.letsPlot.platf.w3c.mapping.svg.domExtensions.*

object DomUtil {
    fun elementChildren(e: Element): MutableList<Node?> {
        return nodeChildren(e)
    }

    fun nodeChildren(n: Node): MutableList<Node?> {
        return object: AbstractMutableList<Node?>() {

            override val size: Int
                get() = n.childCount

            override fun get(index: Int): Node? = n.getChild(index)

            override fun set(index: Int, element: Node?): Node {
                if (element!!.parentNode != null) {
                    throw IllegalStateException()
                }

                val child = get(index)!!
                n.replaceChild(child, element)
                return child
            }

            override fun add(index: Int, element: Node?) {
                if (element!!.parentNode != null) {
                    throw IllegalStateException()
                }

                if (index == 0) {
                    n.insertFirst(element)
                } else {
                    val prev = n.getChild(index - 1)
                    n.insertAfter(element, prev)
                }
            }

            override fun removeAt(index: Int): Node {
                val child = n.getChild(index)!!
                n.removeChild(child)
                return child
            }
        }
    }

    fun <NodeT, ElementT : With<out NodeT>> withElementChildren(base: MutableList<NodeT?>): List<ElementT?> {
        val items: MutableList<ElementT?> = mutableListOf()

        return object: AbstractMutableList<ElementT?>() {
            override val size: Int
                get() = items.size

            override fun get(index: Int): ElementT? = items[index]

            override fun set(index: Int, element: ElementT?): ElementT {
                val result: ElementT? = items.set(index, element)
                base[index] = result!!.getElement()
                return result
            }

            override fun add(index: Int, element: ElementT?) {
                items.add(index, element)
                base.add(index, element!!.getElement())
            }

            override fun removeAt(index: Int): ElementT? {
                val result = items.removeAt(index)
                base.removeAt(index)
                return result
            }
        }
    }

    fun innerTextOf(e: Element): WritableProperty<String> {
        return object : WritableProperty<String> {
            override fun set(value: String) {
                e.innerHTML = value
            }
        }
    }

    fun checkbox(element: HTMLInputElement): Property<Boolean> {
        return object : Property<Boolean> {
            private var myTimerRegistration: Registration? = null
            private val myListeners: Listeners<EventHandler<PropertyChangeEvent<Boolean>>> = Listeners()

            override val propExpr: String
                get() = "checkbox($element)"

            override fun get(): Boolean = element.checked

            override fun set(value: Boolean) {
                element.checked = value
            }

            override fun addHandler(handler: EventHandler<PropertyChangeEvent<out Boolean>>): Registration {
                if (myListeners.isEmpty) {
                    val value: Value<Boolean> = Value(element.checked)
                    val timer = window.setInterval({
                        val currentValue = element.checked
                        if (currentValue != value.get()) {
                            myListeners.fire(object : ListenerCaller<EventHandler<PropertyChangeEvent<Boolean>>> {
                                override fun call(l: EventHandler<PropertyChangeEvent<Boolean>>) {
                                    l.onEvent(PropertyChangeEvent(value.get(), currentValue))
                                }
                            })
                            value.set(currentValue)
                        }
                    })

                    myTimerRegistration = object : Registration() {
                        override fun doRemove() {
                            window.clearInterval(timer)
                        }
                    }
                }

                val reg = myListeners.add(handler)
                return object : Registration() {
                    override fun doRemove() {
                        reg.remove()
                        if (myListeners.isEmpty) {
                            myTimerRegistration!!.remove()
                            myTimerRegistration = null
                        }
                    }
                }
            }
        }
    }

    fun hasClass(el: Element, cls: String): WritableProperty<Boolean> {
        return object : WritableProperty<Boolean> {
            private var myValue: Boolean? = null

            override fun set(value: Boolean) {
                if (myValue == value) return
                if (value) {
                    el.addClass(cls)
                } else {
                    el.removeClass(cls)
                }
                myValue = value
            }
        }
    }

    fun attribute(el: Element, attr: String): WritableProperty<String> {
        return object : WritableProperty<String> {
            override fun set(value: String) {
                el.setAttribute(attr, value)
            }
        }
    }

    fun hasAttribute(el: Element, attr: String, attrValue: String): WritableProperty<Boolean> {
        return object : WritableProperty<Boolean> {
            override fun set(value: Boolean) {
                if (value) {
                    el.setAttribute(attr, attrValue)
                } else {
                    el.removeAttribute(attr)
                }
            }
        }
    }

    fun visibilityOf(el: HTMLElement): WritableProperty<Boolean> {
        return object : WritableProperty<Boolean> {
            override fun set(value: Boolean) {
                if (value) {
                    el.style.clearDisplay()
                } else {
                    el.style.display = CssDisplay.NONE
                }
            }
        }
    }

    fun dimension(el: Element): ReadableProperty<Vector?> {
        return timerBasedProperty(object : Supplier<Vector> {
            override fun get(): Vector = Vector(el.clientWidth, el.clientHeight)
        }, 200)
    }

    fun <ValueT> timerBasedProperty(supplier: Supplier<ValueT>, period: Int): ReadableProperty<ValueT?> {
        return object : UpdatableProperty<ValueT?>() {
            private var myTimer: Int = -1

            override fun doAddListeners() {
                myTimer = window.setInterval({update()}, period)
            }

            override fun doRemoveListeners() {
                window.clearInterval(myTimer)
            }

            override fun doGet(): ValueT? = supplier.get()
        }
    }

    fun generateElement(source: SvgElement): SVGElement =
            when(source) {
                is SvgEllipseElement -> createSVGElement("ellipse")
                is SvgCircleElement -> createSVGElement("circle")
                is SvgRectElement -> createSVGElement("rect")
                is SvgTextElement -> createSVGElement("text")
                is SvgPathElement -> createSVGElement("path")
                is SvgLineElement -> createSVGElement("line")
                is SvgSvgElement -> createSVGElement("svg")
                is SvgGElement -> createSVGElement("g")
                is SvgStyleElement -> createSVGElement("style")
                is SvgTSpanElement -> createSVGElement("tspan")
                is SvgDefsElement -> createSVGElement("defs")
                is SvgClipPathElement -> createSVGElement("clipPath")
                is SvgImageElement -> createSVGElement("image")
                else -> throw IllegalStateException("Unsupported svg element ${source::class.simpleName}")
            }

    fun generateSlimNode(source: SvgSlimNode): Element =
            when(source.elementName) {
                SvgSlimElements.GROUP -> createSVGElement("g")
                SvgSlimElements.LINE -> createSVGElement("line")
                SvgSlimElements.CIRCLE -> createSVGElement("circle")
                SvgSlimElements.RECT -> createSVGElement("rect")
                SvgSlimElements.PATH -> createSVGElement("path")
                else -> throw IllegalStateException("Unsupported SvgSlimNode ${source::class}")
            }

    @Suppress("UNUSED_PARAMETER")
    fun generateTextElement(source: SvgTextNode): Text = document.createTextNode("")

    private fun createSVGElement(name: String): SVGElement =
            document.createElementNS(SVG_NAMESPACE_URI, name) as SVGElement
}
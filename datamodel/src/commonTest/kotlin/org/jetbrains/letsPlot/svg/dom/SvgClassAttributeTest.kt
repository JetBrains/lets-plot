/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.svg.dom

import jetbrains.datalore.base.function.Value
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAttributeSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgElementListener
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgEllipseElement
import org.jetbrains.letsPlot.datamodel.svg.event.SvgAttributeEvent
import kotlin.test.*

class SvgClassAttributeTest {

    companion object {
        private const val cl = "class"
        private const val altCl = "alt-class"
    }

    private val illegalCl = "$cl $altCl"

    private val element = SvgEllipseElement()

    private fun attachEventFlag(): Value<Boolean> {
        val classReplaced = Value(false)
        element.addClass("init")
        element.addClass(cl)
        element.addListener(object : SvgElementListener {
            override fun onAttrSet(event: SvgAttributeEvent<*>) {
                if (event.attrSpec == SvgAttributeSpec.createSpec<Any>("class")) {
                    classReplaced.set(true)
                }
            }
        })

        return classReplaced
    }

    @Test
    fun empty() {
        assertSame("", element.fullClass())
        assertNull(element.classAttribute().get())
    }

    @Test
    fun addClass() {
        assertFalse(element.hasClass(cl))
        assertTrue(element.addClass(cl))
        assertTrue(element.hasClass(cl))
    }

    @Test
    fun addExistingClass() {
        assertTrue(element.addClass(cl))
        assertFalse(element.addClass(cl))
    }

    @Test
    fun removeClass() {
        element.addClass(cl)
        assertTrue(element.removeClass(cl))
        assertFalse(element.hasClass(cl))
    }

    @Test
    fun removeNonexistentClass() {
        assertFalse(element.removeClass(cl))
    }

    @Test
    fun replaceClass() {
        element.addClass(cl)
        element.replaceClass(
            cl,
            altCl
        )
        assertFalse(element.hasClass(cl))
        assertTrue(element.hasClass(altCl))
    }

    @Test
    fun replaceEmptyClass() {
        assertFailsWith(IllegalStateException::class) {
            element.replaceClass(
                cl,
                altCl
            )
        }
    }

    @Test
    fun replaceNonexistentClassException() {
        assertFailsWith(IllegalStateException::class) {
            element.addClass(cl)
            element.replaceClass(
                altCl,
                cl
            )
        }
    }

    @Test
    fun toggleClass() {
        assertTrue(element.toggleClass(cl))
        assertTrue(element.hasClass(cl))
        assertTrue(element.toggleClass(altCl))
        assertTrue(element.hasClass(altCl))
        assertFalse(element.toggleClass(cl))
        assertFalse(element.hasClass(cl))
    }

    @Test
    fun addIllegalClassException() {
        assertFailsWith(IllegalArgumentException::class) {
            element.addClass(illegalCl)
        }
    }

    @Test
    fun removeIllegalClassException() {
        assertFailsWith(IllegalArgumentException::class) {
            element.addClass(cl)
            element.addClass(altCl)
            element.removeClass(illegalCl)
        }
    }

    @Test
    fun replaceIllegalClassException() {
        assertFailsWith(IllegalArgumentException::class) {
            element.addClass(cl)
            element.addClass(altCl)
            element.replaceClass(illegalCl, cl)
        }
    }

    @Test
    fun toggleIllegalClassException() {
        assertFailsWith(IllegalArgumentException::class) {
            element.toggleClass(illegalCl)
        }
    }

    @Test
    fun eventsTriggerOnAdd() {
        val classAdded = Value(false)
        element.addClass("init")
        element.addListener(object : SvgElementListener {
            override fun onAttrSet(event: SvgAttributeEvent<*>) {
                if (event.attrSpec == SvgAttributeSpec.createSpec<SvgAttributeSpec<Any>>("class")) {
                    classAdded.set(true)
                }
            }
        })

        element.addClass(cl)
        assertTrue(classAdded.get())
    }

    @Test
    fun eventsTriggerOnRemove() {
        val classRemoved = attachEventFlag()
        element.removeClass(cl)
        assertTrue(classRemoved.get())
    }

    @Test
    fun eventsTriggerOnReplace() {
        val classReplaced = attachEventFlag()
        element.replaceClass(
            cl,
            altCl
        )
        assertTrue(classReplaced.get())
    }

    @Test
    fun eventsTriggerOnToggle() {
        val classToggled = Value(false)
        element.addListener(object : SvgElementListener {
            override fun onAttrSet(event: SvgAttributeEvent<*>) {
                if (event.attrSpec == SvgAttributeSpec.createSpec<SvgAttributeSpec<Any>>("class")) {
                    classToggled.set(true)
                }
            }
        })
        element.toggleClass(cl)
        assertTrue(classToggled.get())

        classToggled.set(false)
        element.toggleClass(cl)
        assertTrue(classToggled.get())
    }
}
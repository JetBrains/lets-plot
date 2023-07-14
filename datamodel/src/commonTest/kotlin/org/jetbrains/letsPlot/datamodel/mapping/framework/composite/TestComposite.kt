/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.composite

import org.jetbrains.letsPlot.commons.geometry.Rectangle
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.observable.children.ChildList
import org.jetbrains.letsPlot.commons.intern.observable.children.SimpleComposite
import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.ValueProperty
import org.jetbrains.letsPlot.datamodel.mapping.framework.composite.*

internal open class TestComposite :
        SimpleComposite<TestComposite?, TestComposite>(),
    NavComposite<TestComposite>,
    HasVisibility,
    HasFocusability, HasBounds {


    private val myChildren =
        ChildList<TestComposite, TestComposite>(this)
    private val myVisible = ValueProperty(true)
    private val myFocusable = ValueProperty(true)
    override var bounds = Rectangle(Vector.ZERO, Vector.ZERO)

    override val parent: TestComposite?
        get() = parent().get()

    override fun children(): MutableList<TestComposite> {
        return myChildren
    }

    override fun nextSibling(): TestComposite? {
        return Composites.nextSibling(this)
    }

    override fun prevSibling(): TestComposite? {
        return Composites.prevSibling(this)
    }

    override fun firstChild(): TestComposite? {
        return if (myChildren.isEmpty()) null else myChildren[0]
    }

    override fun lastChild(): TestComposite? {
        return if (myChildren.isEmpty()) null else myChildren[myChildren.size - 1]
    }

    override fun visible(): Property<Boolean> {
        return myVisible
    }

    override fun focusable(): Property<Boolean> {
        return myFocusable
    }

    companion object {

        fun create(x: Int, y: Int, width: Int, height: Int): TestComposite {
            val composite = TestComposite()
            composite.bounds = Rectangle(x, y, width, height)
            return composite
        }
    }
}
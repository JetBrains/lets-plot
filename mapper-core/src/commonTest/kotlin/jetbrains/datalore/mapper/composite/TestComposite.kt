/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

import jetbrains.datalore.base.geometry.Rectangle
import jetbrains.datalore.base.geometry.Vector
import jetbrains.datalore.base.observable.children.ChildList
import jetbrains.datalore.base.observable.children.SimpleComposite
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty

internal open class TestComposite :
        SimpleComposite<TestComposite?, TestComposite>(),
    NavComposite<TestComposite>,
    HasVisibility,
    HasFocusability, HasBounds {


    private val myChildren = ChildList<TestComposite, TestComposite>(this)
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
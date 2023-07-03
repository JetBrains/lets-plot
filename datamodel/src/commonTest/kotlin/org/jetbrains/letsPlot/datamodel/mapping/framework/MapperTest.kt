/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import jetbrains.datalore.base.observable.collections.set.ObservableSet
import kotlin.js.JsName
import kotlin.test.*

class MapperTest {
    private val source = createItemTree()
    private var mapper = ItemMapper(source)
    private var target = mapper.target

    @BeforeTest
    fun init() {
        mapper.attachRoot()
    }

    @Test
    fun initialMapping() {
        assertMapped()
    }

    @Test
    fun propertyChange() {
        source.name.set("xyz")

        assertMapped()
    }

    @Test
    fun removeItemFromObservable() {
        source.observableChildren.removeAt(0)

        assertMapped()
    }

    @Test
    fun addItemToObservable() {
        source.observableChildren.add(0, Item())

        assertMapped()
    }

    @Test
    fun removeItemFromSimple() {
        source.children.removeAt(0)
        mapper.refreshSimpleRole()

        assertMapped()
    }

    @Test
    fun addItemToSimple() {
        source.children.add(Item())
        mapper.refreshSimpleRole()

        assertMapped()
    }

    @Test
    fun singleChildSet() {
        source.singleChild.set(Item())

        assertMapped()
    }

    @Test
    fun singleChildSetToNull() {
        source.singleChild.set(Item())
        source.singleChild.set(null)

        assertMapped()
    }

    @Test
    fun rolesAreCleanedOnDetach() {
        assertFalse(target.observableChildren.isEmpty())
        assertFalse(target.children.isEmpty())
        assertFalse(target.transformedChildren.isEmpty())

        mapper.detachRoot()

        assertTrue(target.observableChildren.isEmpty())
        assertTrue(target.children.isEmpty())
        assertTrue(target.transformedChildren.isEmpty())
    }

    @Test
    fun illegalStateExceptionOnDetachBug() {
        val mapper = TestMapper(Any())
        mapper.attachRoot()

        mapper.attachChild()
        mapper.detachChild()
        mapper.attachChild()

        mapper.detachRoot()
    }

    @Test
    fun illegalStateExceptionOnDetachBugInCaseOfClearCall() {
        val mapper = TestMapper(Any())
        mapper.attachRoot()

        mapper.attachChild()
        mapper.detachChildren()
        mapper.attachChild()

        mapper.detachRoot()
    }

/*    @Test
    fun findableRoot() {
        val o = Any()
        val mapper = TestMapper(o)
        mapper.attachRoot()
        assertSame(mapper, mapper.getDescendantMapper(o))

        mapper.detachRoot()
    }*/

    @Test
    fun nonFindableRoot() {
        val o = Any()
        val mapper = object : TestMapper(o) {
            override val isFindable: Boolean
                get() = false
        }
        mapper.attachRoot()
        assertNull(mapper.getDescendantMapper(o))

        mapper.detachRoot()
    }

/*
    @Test
    fun mappingContextListeners() {
        val l = Mockito.mock(MappingContextListener::class.java)

        val ctx = MappingContext()
        ctx.addListener(l)

        val mapper = TestMapper(Any())
        mapper.attachRoot(ctx)
        mapper.detachRoot()

        Mockito.verify(l).onMapperRegistered(mapper)
        Mockito.verify(l).onMapperUnregistered(mapper)
    }
*/

    private fun assertMapped() {
        assertTrue(source.contentEquals(target))
    }

    private fun createItemTree(): Item {
        val result = Item()
        result.name.set("xyz")

        for (i in 0..2) {
            val child = Item()
            child.name.set("child$i")
            result.observableChildren.add(child)
            result.children.add(child)
            result.transformedChildren.add(child)
        }

        return result
    }

    internal open class TestMapper internal constructor(source: Any) : Mapper<Any, Any>(source, Any()) {
        @JsName("childrenSet")   // `parent` clashes with HasParent.parent
        private val children: ObservableSet<Mapper<*, *>> = createChildSet()
        private var child: TestMapper? = null

        internal fun attachChild() {
            child = TestMapper(Any())
            children.add(child!!)
        }

        internal fun detachChild() {
            children.remove(child!!)
            child = null
        }

        internal fun detachChildren() {
            children.clear()
            child = null
        }
    }
}
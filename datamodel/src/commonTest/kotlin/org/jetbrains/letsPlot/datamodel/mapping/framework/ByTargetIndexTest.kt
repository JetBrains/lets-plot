/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ByTargetIndexTest {

    private val sourceToTarget = HashMap<Item, Item>()

    private val item: Item = Item()
    private val hiddenItem: Item = Item()
    private val child: Item = Item()
    private val hiddenChild: Item = Item()
    private lateinit var finder: ByTargetIndex

    @BeforeTest
    fun init() {
//        item = Item()
//        child = Item()
//        hiddenChild = Item()
        item.observableChildren.add(child)
        val mapper = MyItemMapper(item)
        mapper.attachRoot()

//        hiddenItem = Item()
        hiddenItem.observableChildren.add(hiddenChild)
        mapper.createChildProperty<Mapper<*, *>>().set(MyNotFindableItemMapper(hiddenItem))

        finder = ByTargetIndex(mapper.mappingContext!!)
    }

    @Test
    fun findMapper() {
        assertFound(child)
        assertNotFound(hiddenChild)
    }

    @Test
    fun removeMapper() {
        item.observableChildren.remove(child)

        assertNotFound(child)

        // no exception is thrown
        hiddenItem.observableChildren.remove(hiddenChild)
    }

    @Test
    fun addMapper() {
        var anotherChild = Item()
        child.observableChildren.add(anotherChild)

        assertFound(anotherChild)

        anotherChild = Item()
        hiddenChild.observableChildren.add(anotherChild)
        assertNotFound(anotherChild)
    }

    @Test
    fun addThenRemoveMapper() {
        val anotherChild = Item()
        child.observableChildren.add(anotherChild)
        child.observableChildren.remove(anotherChild)

        assertNotFound(anotherChild)
    }

    @Test
    fun addWhenFinderDisposed() {
        finder.dispose()

        val anotherChild = Item()
        child.observableChildren.add(anotherChild)

        assertNotFound(anotherChild)
    }

    private fun assertFound(child: Item) {
        val target = sourceToTarget[child]
        assertEquals(1, finder.getMappers(target!!).size)
    }

    private fun assertNotFound(child: Item) {
        val target = sourceToTarget[child]
        if (target != null) {
            assertEquals(0, finder.getMappers(target).size)
        }
    }

    private open inner class MyItemMapper(item: Item) : ItemMapper(item) {
        init {
            sourceToTarget[item] = target
        }

        override fun createMapperFactory(): MapperFactory<Item, Item> {
            return object : MapperFactory<Item, Item> {
                override fun createMapper(source: Item): Mapper<out Item, out Item> {
                    return MyItemMapper(source)
                }
            }
        }
    }

    private inner class MyNotFindableItemMapper(item: Item) : MyItemMapper(item) {

        override val isFindable: Boolean
            get() = false

        override fun createMapperFactory(): MapperFactory<Item, Item> {
            return object : MapperFactory<Item, Item> {
                override fun createMapper(source: Item): Mapper<out Item, out Item> {
                    return object : ItemMapper(source) {
                        override val isFindable: Boolean
                            get() = false
                    }
                }
            }
        }
    }
}

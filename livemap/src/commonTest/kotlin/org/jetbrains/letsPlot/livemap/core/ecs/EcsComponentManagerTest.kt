/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

import kotlin.test.*


class EcsComponentManagerTest {

    class Foo: EcsComponent
    class Bar: EcsComponent
    class Baz: EcsComponent
    class Qux: EcsComponent

    private lateinit var man: EcsComponentManager
    private lateinit var foo: EcsEntity
    private lateinit var bar: EcsEntity
    private lateinit var baz: EcsEntity
    private lateinit var fooBar: EcsEntity
    private lateinit var fooBaz: EcsEntity
    private lateinit var barBaz: EcsEntity


    @BeforeTest
    fun setUp() {
        man = EcsComponentManager()
        foo = man.createEntity("foo").addComponents { + Foo() }
        bar = man.createEntity("bar").addComponents { + Bar() }
        baz = man.createEntity("baz").addComponents { + Baz() }
        fooBar = man.createEntity("fooBar").addComponents { + Foo(); + Bar() }
        fooBaz = man.createEntity("fooBaz").addComponents { + Foo(); + Baz() }
        barBaz = man.createEntity("barBaz").addComponents { + Bar(); + Baz() }
    }

    @Test
    fun entity_Remove_WorksViaIterator_ShouldWork() {
        man.createEntity("").addComponents { + Foo() }
        man.createEntity("").addComponents { + Foo() }
        man.createEntity("").addComponents { + Foo() }
        man.createEntity("").addComponents { + Foo() }

        man.getEntities(Foo::class).forEach(EcsEntity::remove)
        man.doRemove()
        assertEquals(3, man.entitiesCount)
    }

    @Test
    fun entity_RemoveComponent_WorksViaCopy_ShouldWork() {
        man.createEntity("").addComponents { + Foo() }
        man.createEntity("").addComponents { + Foo() }
        man.createEntity("").addComponents { + Foo() }
        man.createEntity("").addComponents { + Foo() }
        man.getEntities(Foo::class).toList().forEach {
            it.removeComponent(Foo::class)
        }
        assertEquals(0, man.getEntities(Foo::class).count())
    }

    @Test
    fun getEntities() {
        // Same components order
        assertEquals(setOf(fooBar), man.getEntities(listOf(Foo::class, Bar::class)).toSet())
        assertEquals(setOf(barBaz), man.getEntities(listOf(Bar::class, Baz::class)).toSet())

        // Starts with same component
        assertEquals(setOf(fooBar), man.getEntities(listOf(Bar::class, Foo::class)).toSet())
        assertEquals(setOf(barBaz), man.getEntities(listOf(Bar::class, Baz::class)).toSet())

        // Partial match
        assertEquals(0, man.getEntities(listOf(Foo::class, Qux::class)).count())
        assertEquals(0, man.getEntities(listOf(Qux::class, Bar::class)).count())
    }

    @Test
    fun getEntities_WithRemoved() {
        man.removeComponent(fooBar, Foo::class)

        assertEquals(0, man.getEntities(listOf(Foo::class, Bar::class)).count())
        assertEquals(setOf(foo, fooBaz), man.getEntities(listOf(Foo::class)).toSet())
    }

    @Test
    fun getEntityById_IfNotExist_ShouldThrow() {
        man.removeEntity(foo)

        assertFailsWith(NullPointerException::class) {
            man.getEntityById(foo.id)
        }
    }

    @Test
    fun getEntitiesById_ShouldSkipRemoved() {
        man.removeEntity(foo)
        man.removeEntity(baz)

        assertEquals(setOf(bar), man.getEntitiesById(listOf(foo.id, bar.id, baz.id)).toSet())
    }

    @Test
    fun getEntitiesById_ShouldSkipNonExisting() {
        assertEquals(setOf(bar, baz), man.getEntitiesById(listOf(bar.id, baz.id, 777, 888)).toSet())
    }

    @Test
    fun getEntity_Foo_ReturnsFirstEntity_ContainingFoo() {
        assertTrue(setOf(foo, fooBar, fooBaz).contains(man.getEntity(Foo::class)))
    }

    @Test
    fun getSingletonEntity_ForMultiplyEntities_ThrowsException() {
        assertFailsWith(IllegalStateException::class) { man.getSingletonEntity(Foo::class) }
    }

    @Test
    fun getSingletonEntity_() {
        assertEquals(fooBar, man.getSingletonEntity(listOf(Foo::class, Bar::class)))
    }

    @Test
    fun countComponents_WithRemovedEntities() {
        assertEquals(3, man.count(Foo::class))
        man.removeEntity(foo)
        assertEquals(2, man.count(Foo::class))
    }

    @Test
    fun containsEntity() {
        assertTrue(man.containsEntity(foo))
        man.removeEntity(foo)
        assertFalse(man.containsEntity(foo))
    }

    @Test
    fun getComponents() {
        assertEquals(setOf(Foo::class, Bar::class), man.getComponents(fooBar).keys)
        man.removeEntity(fooBar)
        assertEquals(0, man.getComponents(fooBar).count())
    }
}

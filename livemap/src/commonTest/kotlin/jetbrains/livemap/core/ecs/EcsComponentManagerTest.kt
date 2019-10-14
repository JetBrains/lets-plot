package jetbrains.livemap.core.ecs

import kotlin.test.*


class EcsComponentManagerTest {

    class Foo: EcsComponent
    class Bar: EcsComponent
    class Baz: EcsComponent
    class Qux: EcsComponent

    lateinit var man: EcsComponentManager
    lateinit var foo: EcsEntity
    lateinit var bar: EcsEntity
    lateinit var baz: EcsEntity
    lateinit var fooBar: EcsEntity
    lateinit var fooBaz: EcsEntity
    lateinit var barBaz: EcsEntity


    @BeforeTest
    fun setUp() {
        man = EcsComponentManager()
        foo = man.createEntity("foo").addComponent(Foo())
        bar = man.createEntity("bar").addComponent(Bar())
        baz = man.createEntity("baz").addComponent(Baz())
        fooBar = man.createEntity("fooBar").addComponent(Foo()).addComponent(Bar())
        fooBaz = man.createEntity("fooBaz").addComponent(Foo()).addComponent(Baz())
        barBaz = man.createEntity("barBaz").addComponent(Bar()).addComponent(Baz())
    }

    @Test
    fun entity_Remove_WorksViaIterator_ShouldWork() {
        man.createEntity("").addComponent(Foo())
        man.createEntity("").addComponent(Foo())
        man.createEntity("").addComponent(Foo())
        man.createEntity("").addComponent(Foo())

        man.getEntities(Foo::class).forEach(EcsEntity::remove)
        assertEquals(0, man.entitiesCount)
    }

    @Test
    fun entity_RemoveComponent_WorksViaCopy_ShouldWorkd() {
        man.createEntity("").addComponent(Foo())
        man.createEntity("").addComponent(Foo())
        man.createEntity("").addComponent(Foo())
        man.createEntity("").addComponent(Foo())
        man.getEntities(Foo::class).toList().forEach {
            it.removeComponent(Foo::class)
        }
        assertEquals(0, man.entitiesCount)
    }

    @Test
    fun getEntities() {
        // Same components order
        assertEquals(fooBar, man.getEntities(listOf(Foo::class, Bar::class)).first())
        assertEquals(barBaz, man.getEntities(listOf(Bar::class, Baz::class)).first())

        // Starts with same component
        assertEquals(fooBar, man.getEntities(listOf(Bar::class, Foo::class)).first())
        assertEquals(barBaz, man.getEntities(listOf(Bar::class, Baz::class)).first())

        // Partial match
        assertEquals(0, man.getEntities(listOf(Foo::class, Qux::class)).count())
        assertEquals(0, man.getEntities(listOf(Qux::class, Bar::class)).count())
    }

    @Test
    fun getEntities_WithRemoved() {
        fooBar.removeComponent(Bar::class)

        assertEquals(0, man.getEntities(listOf(Foo::class, Bar::class)).count())
        assertEquals(fooBaz, man.getEntities(listOf(Foo::class)).first())
    }

    @Test
    fun getEntityById_IfNotExist_ShouldThrow() {
        man.removeEntity(foo)

        assertFailsWith(NullPointerException::class) {
            man.getEntityById(foo.id)
        }
    }

    @Test
    fun getEntitiesById_IfNotExist_ShouldSkip() {
        man.removeEntity(foo)
        man.removeEntity(baz)

        assertEquals(listOf(bar), man.getEntitiesById(listOf(foo.id, bar.id, baz.id)).toList())
    }

    @Test
    fun getEntity_Foo_ReturnsFirstEntity_ContainingFoo() {
        assertTrue(listOf(foo, fooBar, fooBaz).contains(man.getEntity(Foo::class)))
    }

    @Test
    fun getSingletonEntity_ForMultiplyEntities_ThrowsException() {
        assertFailsWith(IllegalStateException::class) { man.getSingletonEntity(Foo::class) }
    }

    @Test
    fun countComponents_WithRemovedEntities() {
        assertEquals(3, man.getComponentsCount(Foo::class))
        man.removeEntity(foo)
        assertEquals(2, man.getComponentsCount(Foo::class))
    }

    @Test
    fun containsEntity() {
        assertTrue(man.containsEntity(foo))
        man.removeEntity(foo)
        assertFalse(man.containsEntity(foo))
    }

    @Test
    fun getComponents() {
        assertEquals(2, man.getComponents(fooBar).count())
        man.removeEntity(fooBar)
        assertEquals(0, man.getComponents(fooBar).count())
    }
}

package jetbrains.datalore.mapper.core

import jetbrains.datalore.base.gcommon.collect.Iterables
import jetbrains.datalore.base.registration.Registration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PartIteratorsTest {
    @Test
    fun synchronizersIterator() {
        val mapper = object : Mapper<Unit, Unit>(Unit, Unit) {
            override fun registerSynchronizers(conf: SynchronizersConfiguration) {
                super.registerSynchronizers(conf)
                conf.add(Synchronizers.forRegistration(Registration.EMPTY))
            }
        }
        assertTrue(Iterables.isEmpty(mapper.synchronizers()))

        mapper.attachRoot()
        assertEquals(1, Iterables.size(mapper.synchronizers()))

        val children = mapper.createChildSet<Mapper<*, *>>()
        children.add(object : Mapper<Unit, Unit>(Unit, Unit) {

        })
        assertEquals(1, Iterables.size(mapper.synchronizers()))
    }

    @Test
    fun childrenIterator() {
        val mapper = object : Mapper<Any, Any>(Any(), Any()) {
            override fun registerSynchronizers(conf: SynchronizersConfiguration) {
                super.registerSynchronizers(conf)
                conf.add(Synchronizers.forRegistration(Registration.EMPTY))
            }
        }

        assertEquals(0, Iterables.size(mapper.children()))

        mapper.attachRoot()
        assertEquals(0, Iterables.size(mapper.children()))
        assertEquals(1, Iterables.size(mapper.synchronizers()))

        mapper.createChildList<Mapper<*, *>>().add(object : Mapper<Any, Any>(Any(), Any()) {

        })
        val setChild = object : Mapper<Any, Any>(Any(), Any()) {

        }
        val childSet = mapper.createChildSet<Mapper<*, *>>()
        childSet.add(setChild)
        val childProperty = mapper.createChildProperty<Mapper<*, *>>()
        childProperty.set(object : Mapper<Any, Any>(Any(), Any()) {

        })
        assertEquals(3, Iterables.size(mapper.children()))

        childProperty.set(null)
        assertEquals(2, Iterables.size(mapper.children()))

        val i = childSet.iterator()
        i.next()
        i.remove()

        assertEquals(1, Iterables.size(mapper.children()))
    }
}
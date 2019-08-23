package jetbrains.livemap.core.ecs

import kotlin.test.Test


class EcsComponentManagerTest {
    @Test
    fun test() {
        val componentManager = EcsComponentManager()
        componentManager.createEntity("").addComponent(TestComponent())
        componentManager.createEntity("").addComponent(TestComponent())
        componentManager.createEntity("").addComponent(TestComponent())
        componentManager.createEntity("").addComponent(TestComponent())

        componentManager.getEntities(TestComponent::class).forEach(EcsEntity::remove)
    }

    @Test
    fun removeComponentTest() {
        val componentManager = EcsComponentManager()
        componentManager.createEntity("").addComponent(TestComponent())
        componentManager.createEntity("").addComponent(TestComponent())
        componentManager.createEntity("").addComponent(TestComponent())
        componentManager.createEntity("").addComponent(TestComponent())


        componentManager.getEntities(TestComponent::class).toList().forEach {
            it.removeComponent(TestComponent::class)
        }
    }

    internal class TestComponent : EcsComponent
}
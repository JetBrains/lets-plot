package jetbrains.livemap.entities.rendering

import jetbrains.livemap.core.ecs.EcsComponent


class LayerEntitiesComponent : EcsComponent {

    private val myEntities = HashSet<Int>()

    val entities: Collection<Int>
        get() = myEntities

    fun add(entity: Int) {
        myEntities.add(entity)
    }

    fun remove(entity: Int) {
        myEntities.remove(entity)
    }
}
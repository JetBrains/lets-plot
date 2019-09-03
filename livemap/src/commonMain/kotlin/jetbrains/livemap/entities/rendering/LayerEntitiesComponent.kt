package jetbrains.livemap.entities.rendering

import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity


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

    companion object {
        operator fun get(entity: EcsEntity): LayerEntitiesComponent {
            return entity.getComponent()
        }

        fun getEntities(entity: EcsEntity): Iterable<EcsEntity> {
            return entity.componentManager.getEntitiesById(entity.get<LayerEntitiesComponent>().entities)
        }
    }
}
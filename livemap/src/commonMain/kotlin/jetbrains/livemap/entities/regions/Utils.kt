package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.livemap.containers.LruCache
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.regions.Components.RegionComponent

object Utils {
    fun entityName(fragmentKey: FragmentKey): String {
        return entityName(fragmentKey.regionId, fragmentKey.quadKey)
    }

    fun entityName(regionId: String, quadKey: QuadKey): String {
        return "fragment_" + regionId + "_" + quadKey.string
    }

    internal fun zoom(fragmentKey: FragmentKey): Int {
        return fragmentKey.quadKey.zoom() // TODO: replace with FragmentKey::zoom and inline
    }

    internal class RegionsIndex(private val myComponentManager: EcsComponentManager) {
        private val myRegionIndex = LruCache<String, Int>(10000)

        fun find(regionId: String): EcsEntity {
            if (myRegionIndex.containsKey(regionId)) {
                return myComponentManager.getEntityById(myRegionIndex[regionId] ?: error(""))
            }

            for (entity in myComponentManager.getEntities(RegionComponent::class)) {
                if (entity.get<RegionComponent>().id.equals(regionId)) {
                    myRegionIndex.put(regionId, entity.id)
                    return entity
                }
            }

            throw IllegalStateException()
        }

    }

    internal class SetBuilder<T>(private val myValues: MutableSet<T>) {

        fun exclude(v: Set<T>): SetBuilder<T> {
            myValues.removeAll(v)
            return this
        }

        fun get(): Set<T> {
            return myValues
        }

        companion object {

            fun ofCopy(requested: Set<FragmentKey>): SetBuilder<FragmentKey> {
                return SetBuilder(HashSet(requested))
            }
        }
    }
}
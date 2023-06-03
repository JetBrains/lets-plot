/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.chart.fragment

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.livemap.containers.LruCache
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geocoding.RegionIdComponent

object Utils {
    fun entityName(fragmentKey: FragmentKey): String {
        return entityName(fragmentKey.regionId, fragmentKey.quadKey)
    }

    fun entityName(regionId: String, quadKey: QuadKey<LonLat>): String {
        return "fragment_" + regionId + "_" + quadKey.key
    }

    internal class RegionsIndex(private val myComponentManager: EcsComponentManager) {
        private val myRegionIndex = LruCache<String, Int>(10000)

        fun find(regionId: String): EcsEntity {
            if (myRegionIndex.containsKey(regionId)) {
                return myComponentManager.getEntityById(myRegionIndex[regionId] ?: error(""))
            }

            for (entity in myComponentManager.getEntities(RegionIdComponent::class)) {
                if (entity.get<RegionIdComponent>().regionId == regionId) {
                    myRegionIndex.put(regionId, entity.id)
                    return entity
                }
            }

            error("")
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
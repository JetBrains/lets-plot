/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.fragments

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.base.typedGeometry.Untyped
import jetbrains.gis.geoprotocol.Boundary
import jetbrains.livemap.chart.fragment.FragmentKey
import jetbrains.livemap.chart.fragment.Utils
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity

class FragmentSpec (private var myKey: FragmentKey) {
    private lateinit var myGeometries: Boundary<Untyped>
    private var myEntity: EcsEntity? = null

    internal constructor(regionId: String, quad: QuadKey<LonLat>) : this(FragmentKey(regionId, quad))

    fun quad(): QuadKey<LonLat> {
        return myKey.quadKey
    }

    fun regionId(): String {
        return myKey.regionId
    }

    fun key(): FragmentKey {
        return myKey
    }

    fun name(): String {
        return Utils.entityName(key())
    }


    fun geometries(): Boundary<Untyped> {
        return myGeometries
    }

    internal fun setGeometries(geometries: Boundary<Untyped>): FragmentSpec {
        myGeometries = geometries
        return this
    }

    fun withReadyEntity(componentManager: EcsComponentManager): FragmentSpec {
        myEntity = componentManager.createEntity(name())
        return this
    }

    fun readyEntity(): EcsEntity? {
        require(!myGeometries.asMultipolygon().isEmpty())
        return myEntity
    }

    companion object {
        fun quads(vararg specs: FragmentSpec): Iterable<QuadKey<LonLat>> {
            return listOf(*specs).map(FragmentSpec::quad)
        }
    }
}
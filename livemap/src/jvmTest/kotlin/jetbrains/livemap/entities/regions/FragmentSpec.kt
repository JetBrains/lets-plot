/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.entities.regions

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.gis.geoprotocol.Boundary
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.entities.regions.FragmentKey
import jetbrains.livemap.entities.regions.Utils.entityName

class FragmentSpec (private var myKey: FragmentKey) {
    private var myGeometries: Boundary<Generic>? = null
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
        return entityName(key())
    }


    fun geometries(): Boundary<Generic>? {
        return myGeometries
    }

    internal fun setGeometries(geometries: Boundary<Generic>): FragmentSpec {
        myGeometries = geometries
        return this
    }

    fun withReadyEntity(componentManager: EcsComponentManager): FragmentSpec {
        myEntity = componentManager.createEntity(name())
        return this
    }

    fun readyEntity(): EcsEntity? {
        checkArgument(myGeometries != null && !myGeometries!!.asMultipolygon().isEmpty())
        return myEntity
    }

    companion object {
        fun quads(vararg specs: FragmentSpec): Array<QuadKey<LonLat>> {
            return listOf(*specs).map { it.quad() }.toTypedArray()
        }
    }
}
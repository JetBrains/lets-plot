/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.chart.fragments

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.livemap.chart.fragment.CachedFragmentsComponent
import jetbrains.livemap.chart.fragment.EmittedFragmentsComponent
import jetbrains.livemap.chart.fragment.EmptyFragmentsComponent
import jetbrains.livemap.chart.fragment.StreamingFragmentsComponent
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.WorldGeometryComponent
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions

internal class FragmentAssert(fragmentSpec: FragmentSpec?, private val myTestBase: LiveMapTestBase) :
    AbstractAssert<FragmentAssert?, FragmentSpec?>(fragmentSpec, FragmentAssert::class.java) {
    private fun findEntity(): Boolean {
        return myTestBase.findEntity(actual!!.name())
    }

    private val entity: EcsEntity
        get() = myTestBase.getEntity(actual!!.name())

    fun haveEntity(): FragmentAssert {
        Assertions.assertThat(findEntity()).isTrue
        return this
    }

    fun doesNotHaveEntity(): FragmentAssert {
        Assertions.assertThat(findEntity()).isFalse
        return this
    }

    fun haveWorldGeometry(): FragmentAssert {
        Assertions.assertThat(entity.contains(WorldGeometryComponent::class)).isTrue()
        return this
    }

    fun doesNotHaveWorldGeometry(): FragmentAssert {
        Assertions.assertThat(entity.contains(WorldGeometryComponent::class)).isFalse()
        return this
    }

    val isStreaming: FragmentAssert
        get() {
            Assertions.assertThat(
                myTestBase.getSingletonComponent<StreamingFragmentsComponent>().keys()
            ).contains(actual!!.key())
            return this
        }
    val isReady: FragmentAssert
        get() {
            Assertions.assertThat(
                myTestBase.getSingletonComponent<CachedFragmentsComponent>().contains(actual!!.key())
            ).isTrue()
            return this
        }
    val isNotReady: FragmentAssert
        get() {
            Assertions.assertThat(
                myTestBase.getSingletonComponent<CachedFragmentsComponent>().contains(actual!!.key())
            ).isFalse()
            return this
        }
    val isNotStreaming: FragmentAssert
        get() {
            Assertions.assertThat(myTestBase.getSingletonComponent<StreamingFragmentsComponent>().keys()).doesNotContain(
                actual!!.key()
            )
            return this
        }
    val isEmitted: FragmentAssert
        get() {
            Assertions.assertThat(
                myTestBase.getSingletonComponent<EmittedFragmentsComponent>().keys()
            ).contains(actual!!.key())
            return this
        }
    val isNotEmitted: FragmentAssert
        get() {
            Assertions.assertThat(myTestBase.getSingletonComponent<EmittedFragmentsComponent>().keys()).doesNotContain(
                actual!!.key()
            )
            return this
        }
    val isNotScalable: FragmentAssert
        get() {
            Assertions.assertThat(entity.contains<WorldGeometryComponent>()).isFalse()
            return this
        }
    val isEmpty: FragmentAssert
        get() {
            Assertions.assertThat(
                myTestBase.getSingletonComponent<EmptyFragmentsComponent>()
                    .contains(actual!!.key().regionId, actual.key().quadKey)
            ).isTrue()
            return this
        }
    val isNotEmpty: FragmentAssert
        get() {
            Assertions.assertThat(
                myTestBase.getSingletonComponent<EmptyFragmentsComponent>()
                    .contains(actual!!.key().regionId, actual.key().quadKey)
            ).isFalse()
            return this
        }

}
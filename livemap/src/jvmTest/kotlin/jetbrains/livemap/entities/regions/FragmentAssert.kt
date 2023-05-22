package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.fragment.CachedFragmentsComponent
import jetbrains.livemap.fragment.EmittedFragmentsComponent
import jetbrains.livemap.fragment.EmptyFragmentsComponent
import jetbrains.livemap.fragment.StreamingFragmentsComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat

internal class FragmentAssert(fragmentSpec: FragmentSpec?, private val myTestBase: LiveMapTestBase) :
    AbstractAssert<FragmentAssert?, FragmentSpec?>(fragmentSpec, FragmentAssert::class.java) {
    private fun findEntity(): Boolean {
        return myTestBase.findEntity(actual!!.name())
    }

    private val entity: EcsEntity
        get() = myTestBase.getEntity(actual!!.name())

    fun haveEntity(): FragmentAssert {
        assertThat(findEntity()).isTrue
        return this
    }

    fun doesNotHaveEntity(): FragmentAssert {
        assertThat(findEntity()).isFalse
        return this
    }

    fun haveWorldGeometry(): FragmentAssert {
        assertThat(entity.contains(WorldGeometryComponent::class)).isTrue()
        return this
    }

    fun doesNotHaveWorldGeometry(): FragmentAssert {
        assertThat(entity.contains(WorldGeometryComponent::class)).isFalse()
        return this
    }

    val isStreaming: FragmentAssert
        get() {
            assertThat(
                myTestBase.getSingletonComponent<StreamingFragmentsComponent>().keys()
            ).contains(actual!!.key())
            return this
        }
    val isReady: FragmentAssert
        get() {
            assertThat(
                myTestBase.getSingletonComponent<CachedFragmentsComponent>().contains(actual!!.key())
            ).isTrue()
            return this
        }
    val isNotReady: FragmentAssert
        get() {
            assertThat(
                myTestBase.getSingletonComponent<CachedFragmentsComponent>().contains(actual!!.key())
            ).isFalse()
            return this
        }
    val isNotStreaming: FragmentAssert
        get() {
            assertThat(myTestBase.getSingletonComponent<StreamingFragmentsComponent>().keys()).doesNotContain(
                actual!!.key()
            )
            return this
        }
    val isEmitted: FragmentAssert
        get() {
            assertThat(
                myTestBase.getSingletonComponent<EmittedFragmentsComponent>().keys()
            ).contains(actual!!.key())
            return this
        }
    val isNotEmitted: FragmentAssert
        get() {
            assertThat(myTestBase.getSingletonComponent<EmittedFragmentsComponent>().keys()).doesNotContain(
                actual!!.key()
            )
            return this
        }
    val isNotScalable: FragmentAssert
        get() {
            assertThat(entity.contains<WorldGeometryComponent>()).isFalse()
            return this
        }
    val isEmpty: FragmentAssert
        get() {
            assertThat(
                myTestBase.getSingletonComponent<EmptyFragmentsComponent>()
                    .contains(actual!!.key().regionId, actual.key().quadKey)
            ).isTrue()
            return this
        }
    val isNotEmpty: FragmentAssert
        get() {
            assertThat(
                myTestBase.getSingletonComponent<EmptyFragmentsComponent>()
                    .contains(actual!!.key().regionId, actual.key().quadKey)
            ).isFalse()
            return this
        }

}
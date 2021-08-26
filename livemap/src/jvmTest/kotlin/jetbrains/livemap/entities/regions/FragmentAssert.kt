package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.geometry.ScreenGeometryComponent
import jetbrains.livemap.geometry.WorldGeometryComponent
import jetbrains.livemap.regions.CachedFragmentsComponent
import jetbrains.livemap.regions.EmittedFragmentsComponent
import jetbrains.livemap.regions.EmptyFragmentsComponent
import jetbrains.livemap.regions.StreamingFragmentsComponent
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

    fun haveScreenGeometry(): FragmentAssert {
        assertThat(entity.contains(ScreenGeometryComponent::class)).isTrue()
        return this
    }

    fun doesNotHaveScreenGeometry(): FragmentAssert {
        assertThat(entity.contains(ScreenGeometryComponent::class)).isFalse()
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
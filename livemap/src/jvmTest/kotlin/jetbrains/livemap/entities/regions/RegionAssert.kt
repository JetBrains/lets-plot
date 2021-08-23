package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.regions.RegionFragmentsComponent
import org.assertj.core.api.AbstractAssert
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class RegionAssert(entity: EcsEntity?, private val myTestBase: LiveMapTestBase) :
    AbstractAssert<RegionAssert?, EcsEntity?>(entity, RegionAssert::class.java) {
    private fun fragmentEntities(vararg fragments: FragmentSpec): Collection<EcsEntity> {
        val entities: MutableList<EcsEntity> = ArrayList()
        Arrays.stream(fragments).map { obj: FragmentSpec -> obj.name() }.forEach { name: String ->
            try {
                entities.add(myTestBase.getEntity(name))
            } catch (e: NoSuchElementException) {
                throw IllegalStateException("Entity $name is not found")
            }
        }
        return entities
    }

    fun rendersFragments(vararg fragments: FragmentSpec) {
        assertThat(actual!!.get<RegionFragmentsComponent>().fragments)
            .containsExactlyInAnyOrderElementsOf(fragmentEntities(*fragments))
    }
}
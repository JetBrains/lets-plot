package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.datalore.maps.Utils.quad
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.fragment.*
import jetbrains.livemap.geocoding.RegionIdComponent
import jetbrains.livemap.mapengine.camera.CameraInputSystem
import org.junit.Test

class RegionEmitSystemTest : RegionsTestBase() {
    private lateinit var fragmentFoo1: FragmentSpec
    private lateinit var fragmentFoo2: FragmentSpec
    private lateinit var fragmentFoo11: FragmentSpec
    private lateinit var fragmentFoo12: FragmentSpec
    private lateinit var emptyFragmentFoo3: FragmentSpec
    override fun setUp() {
        super.setUp()
        addSystem(CameraInputSystem(componentManager))
        addSystem(RegionEmitSystem(componentManager))
        createEntity("FragmentsChange", ChangedFragmentsComponent())
        createEntity("FragmentsResponse", EmittedFragmentsComponent())
        createEntity("EmptyFragments", EmptyFragmentsComponent())
        createEntity("FragmentsState", CachedFragmentsComponent())
        val parentLayerEntity: EcsEntity = createEntity("layer")
        fragmentFoo1 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_1)
        fragmentFoo2 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_2)
        fragmentFoo11 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_11)
        fragmentFoo12 = fragmentSpecWithGeometry(FOO_REGION_ID, QUAD_12)
        emptyFragmentFoo3 = emptyFragmentSpec(FOO_REGION_ID, QUAD_3)

        // Requirements
        createEntity(
            FOO_REGION_ENTITY_NAME,
            RegionIdComponent(FOO_REGION_ID),
            ParentLayerComponent(parentLayerEntity.id),
            RegionFragmentsComponent(),
        )
    }

    protected override val systemsOrder
        get() = listOf(CameraInputSystem::class, RegionEmitSystem::class, SchedulerSystem::class)

    protected override fun afterUpdateCleanup(): List<MockSpec> {
        return listOf(
            Mocks.changedFragments(this).none(),
            Mocks.emittedFragments(this).none(),
            Mocks.cameraUpdate(this).none()
        )
    }

    @Test
    fun onSameZoomShouldRenderFragmentsOnReady() {
        update(
            Mocks.cameraUpdate(this).zoom(1),
            Mocks.changedFragments(this).requested(fragmentFoo1, fragmentFoo2)
        )
        update(
            Mocks.cachedFragments(this).add(fragmentFoo1, fragmentFoo2),
            Mocks.emittedFragments(this).add(fragmentFoo1, fragmentFoo2)
        )
        assertThatRegion(FOO_REGION_ENTITY_NAME).rendersFragments(fragmentFoo1, fragmentFoo2)
    }

    @Test
    fun emptyFragmentShouldNotBeAttachedToRegionEntity() {
        update(
            Mocks.changedFragments(this).requested(emptyFragmentFoo3)
        )
        update(
            Mocks.emptyFragments(this).add(emptyFragmentFoo3),
            Mocks.emittedFragments(this).add(emptyFragmentFoo3)
        )
        assertThatRegion(FOO_REGION_ENTITY_NAME).rendersFragments()
    }

    @Test
    fun onZoomIn_WhenPrevZoomFragmentsAreReady_ShouldNotAttachThemAndWaitProper() {
        update(
            Mocks.cameraUpdate(this).zoom(1),
            Mocks.changedFragments(this).requested(fragmentFoo1, fragmentFoo2)
        )
        update(
            Mocks.cachedFragments(this).add(fragmentFoo1),
            Mocks.emittedFragments(this).add(fragmentFoo1)
        )
        update(
            Mocks.cameraUpdate(this).zoom(2),
            Mocks.changedFragments(this)
                .obsolete(fragmentFoo1, fragmentFoo2)
                .requested(fragmentFoo11, fragmentFoo12)
        )
        update(
            Mocks.cachedFragments(this).add(fragmentFoo2),
            Mocks.emittedFragments(this).add(fragmentFoo2)
        )

        // Fragments from previous zoom should not be used - we are waiting for new fragments
        assertThatRegion(FOO_REGION_ENTITY_NAME).rendersFragments()
        update(
            Mocks.cachedFragments(this).add(fragmentFoo11, fragmentFoo12),
            Mocks.emittedFragments(this).add(fragmentFoo11, fragmentFoo12)
        )

        // Fragments from current zoom should be attached
        assertThatRegion(FOO_REGION_ENTITY_NAME).rendersFragments(fragmentFoo11, fragmentFoo12)
    }

    @Test
    fun onZoomIn_WhenPrevZoomFragmentsReceivedAfterNew_ShouldNotUseThem() {
        update(
            Mocks.cameraUpdate(this).zoom(1),
            Mocks.changedFragments(this).requested(fragmentFoo1, fragmentFoo2)
        )
        update(
            Mocks.emittedFragments(this).add(fragmentFoo1)
        )
        update(
            Mocks.cameraUpdate(this).zoom(2),
            Mocks.changedFragments(this)
                .obsolete(fragmentFoo1, fragmentFoo2)
                .requested(fragmentFoo11, fragmentFoo12)
        )
        update(
            Mocks.cachedFragments(this).add(fragmentFoo11, fragmentFoo12),
            Mocks.emittedFragments(this).add(fragmentFoo11, fragmentFoo12)
        )

        // New zoom fragments are ready and attached
        assertThatRegion(FOO_REGION_ENTITY_NAME).rendersFragments(fragmentFoo11, fragmentFoo12)

        // Late receive from previous zoom
        update(
            Mocks.cachedFragments(this).add(fragmentFoo2),
            Mocks.emittedFragments(this).add(fragmentFoo2)
        )

        // Should still use new zoom fragments
        assertThatRegion(FOO_REGION_ENTITY_NAME).rendersFragments(fragmentFoo11, fragmentFoo12)
    }

    private fun assertThatRegion(name: String): RegionAssert {
        return RegionAssert(getEntity(name), this)
    }

    companion object {
        private const val FOO_REGION_ID = "123123"
        private val QUAD_1: QuadKey<LonLat> = quad("1")
        private val QUAD_2: QuadKey<LonLat> = quad("2")
        private val QUAD_3: QuadKey<LonLat> = quad("3")
        private val QUAD_11: QuadKey<LonLat> = quad("11")
        private val QUAD_12: QuadKey<LonLat> = quad("12")
        private const val FOO_REGION_ENTITY_NAME = "FOO_Region"
    }
}
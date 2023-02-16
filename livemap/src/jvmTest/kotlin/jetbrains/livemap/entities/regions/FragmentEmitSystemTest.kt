package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec.Companion.quads
import jetbrains.datalore.maps.Utils.empty
import jetbrains.datalore.maps.Utils.quad
import jetbrains.datalore.maps.Utils.square
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.config.createMapProjection
import jetbrains.livemap.core.Projections
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.core.layers.ParentLayerComponent
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.fragment.*
import jetbrains.livemap.geocoding.RegionIdComponent
import jetbrains.livemap.geometry.ScaleComponent
import jetbrains.livemap.geometry.WorldGeometry2ScreenUpdateSystem
import jetbrains.livemap.mapengine.LayerEntitiesComponent
import jetbrains.livemap.mapengine.camera.CameraInputSystem
import jetbrains.livemap.mapengine.placement.ScreenLoopComponent
import jetbrains.livemap.mapengine.placement.WorldDimensionComponent
import jetbrains.livemap.mapengine.placement.WorldOrigin2ScreenUpdateSystem
import jetbrains.livemap.mapengine.placement.WorldOriginComponent
import jetbrains.livemap.mapengine.viewport.ViewportGridStateComponent
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`

class FragmentEmitSystemTest : RegionsTestBase() {
    private lateinit var parentLayerEntity: EcsEntity
    private lateinit var fragmentFoo0: FragmentSpec
    private lateinit var fragmentFoo1: FragmentSpec
    private lateinit var emptyFragmentFoo2: FragmentSpec
    override val systemsOrder
        get() = listOf(
            CameraInputSystem::class,
            FragmentEmitSystem::class,
            WorldOrigin2ScreenUpdateSystem::class,
            WorldGeometry2ScreenUpdateSystem::class,
            SchedulerSystem::class
        )

    override fun afterUpdateCleanup(): List<MockSpec> {
        return listOf(
            Mocks.changedFragments(this).none(),
            Mocks.downloadingFragments(this).none()
        )
    }

    @Before
    override fun setUp() {
        super.setUp()
        createEntity("CellState", ViewportGridStateComponent())
        createEntity("LayerEntities", LayerEntitiesComponent())
        createEntity("FragmentsChange", ChangedFragmentsComponent(), EmptyFragmentsComponent())
        createEntity("DownloadingFragments", DownloadingFragmentsComponent())

        createEntity(
            FOO_REGION_ENTITY_NAME,
            RegionIdComponent(REGION_ID)
        )

        parentLayerEntity = createEntity("layer")
        createEntity(
            FOO_REGION_ENTITY_NAME,
            RegionIdComponent(FOO_REGION_ID),
            ParentLayerComponent(parentLayerEntity.id)
        )
        addSystem(CameraInputSystem(componentManager))
        addSystem(WorldOrigin2ScreenUpdateSystem(componentManager))
        addSystem(WorldGeometry2ScreenUpdateSystem(Int.MAX_VALUE, componentManager))
        addSystem(FragmentEmitSystem(Int.MAX_VALUE, componentManager))
        `when`(liveMapContext.mapProjection).thenReturn(createMapProjection(Projections.mercator()))
        fragmentFoo0 = FragmentSpec(FOO_REGION_ID, QUAD_0).setGeometries(square(1, 2, 30, 40))
        fragmentFoo1 = FragmentSpec(FOO_REGION_ID, QUAD_1).setGeometries(square(5, 6, 70, 80))
        emptyFragmentFoo2 = FragmentSpec(FOO_REGION_ID, QUAD_2).setGeometries(empty())
    }

    @Test
    fun whenNothingToAddShouldDoNothing() {
        update()
    }

    @Test
    fun fragmentComponentsShouldContainCorrectValues() {
        update(
            Mocks.camera(this).position(WorldPoint(0, 0)).zoom(1.0),
            Mocks.downloadingFragments(this).downloaded(fragmentFoo0, fragmentFoo1),
            Mocks.viewportGrid(this).visibleQuads(quads(fragmentFoo0, fragmentFoo1)),
            Mocks.changedFragments(this).requested(fragmentFoo0, fragmentFoo1)
        )
        update(
            waitGeometries()
        )

        run {
            val fragmentEntity: EcsEntity = getEntity(fragmentFoo0.name())
            assertThat(fragmentEntity.contains(EMITTED_FRAGMENT_COMPONENTS)).isTrue
            assertThat(fragmentEntity.get<WorldOriginComponent>().origin).isEqualTo(WorldPoint(x=128.7111111111111, y=95.03156113339311))
            assertThat(fragmentEntity.get<WorldDimensionComponent>().dimension).isEqualTo(WorldPoint(x=21.333333333333314, y=31.545927733930668))
            assertThat(fragmentEntity.get<FragmentComponent>().fragmentKey.quadKey).isEqualTo(quad<LonLat>("0"))
            assertThat(fragmentEntity.get<ParentLayerComponent>().layerId).isEqualTo(parentLayerEntity.id)
        }
        run {
            val fragmentEntity: EcsEntity = getEntity(fragmentFoo1.name())
            assertThat(fragmentEntity.contains(EMITTED_FRAGMENT_COMPONENTS)).isTrue
            assertThat(fragmentEntity.get<WorldOriginComponent>().origin).isEqualTo(WorldPoint(x=131.55555555555554, y=5.4495652533470364E-11))
            assertThat(fragmentEntity.get<WorldDimensionComponent>().dimension).isEqualTo(WorldPoint(x=49.7777777777778, y=123.72551367976955))
            assertThat(fragmentEntity.get<FragmentComponent>().fragmentKey.quadKey).isEqualTo(quad<LonLat>("1"))
            assertThat(fragmentEntity.get<ParentLayerComponent>().layerId).isEqualTo(parentLayerEntity.id)
        }
    }

    @Test
    fun onRequestNotReadyFragment_ShouldFetchAndEmit() {
        update(
            Mocks.downloadingFragments(this).downloaded(fragmentFoo0),
            Mocks.viewportGrid(this).visibleQuads(quads(fragmentFoo0)),
            Mocks.changedFragments(this).requested(fragmentFoo0),
            Mocks.scheduler(this).skipAll()
        )
        update(
            waitGeometries()
        )
        assertThatFragment(fragmentFoo0)
            .haveEntity()
            .isReady
            .isEmitted
            .isNotStreaming
    }

    @Test
    fun onRequestReadyFragment_ShouldEmit() {
        fragmentFoo0.withReadyEntity(componentManager)
        update(
            Mocks.cachedFragments(this).add(fragmentFoo0),
            Mocks.changedFragments(this).requested(fragmentFoo0)
        )
        assertThatFragment(fragmentFoo0).isEmitted
    }

    @Test
    fun shouldMarkFragmentAsStreamingWhileReceivingDataAndTransformingScreenGeometry() {
        update(
            Mocks.changedFragments(this).requested(fragmentFoo0),
            Mocks.scheduler(this).skipAll()
        )
        assertThatFragment(fragmentFoo0)
            .doesNotHaveEntity()
            .isNotEmitted
            .isNotReady

        update(
            Mocks.downloadingFragments(this).downloaded(fragmentFoo0),
            Mocks.viewportGrid(this).visibleQuads(quads(fragmentFoo0)),
            Mocks.scheduler(this).skipAll() // defer geometries processing
        )

        // Fragment entity created, but still transforming world geometry to screen
        assertThatFragment(fragmentFoo0)
            .haveEntity()
            .doesNotHaveScreenGeometry()
            .isNotEmitted
            .isNotReady
        update(waitGeometries())

        // Fragment entity created, but still transforming world geometry to screen
        assertThatFragment(fragmentFoo0)
            .haveEntity()
            .haveScreenGeometry()
            .isNotStreaming
            .isEmitted
            .isReady
    }

    private fun waitGeometries(): Iterable<MockSpec> {
        return listOf(
            Mocks.repeatUpdate(this).times(2),  // process World2Screen systema and remove finished fragments
            Mocks.changedFragments(this).none(),
            Mocks.emittedFragments(this).none()
        )
    }

    private fun assertThatFragment(spec: FragmentSpec?): FragmentAssert {
        return FragmentAssert(spec, this)
    }

    @Test
    fun emptyFragmentsShouldBeStoredAsEmpty() {
        update(
            Mocks.downloadingFragments(this).downloaded(emptyFragmentFoo2),
            Mocks.viewportGrid(this).visibleQuads(quads(emptyFragmentFoo2)),
            Mocks.changedFragments(this).requested(emptyFragmentFoo2)
        )
        assertThatFragment(emptyFragmentFoo2)
            .doesNotHaveEntity()
            .isNotStreaming
            .isNotReady
            .isEmitted
            .isEmpty
    }

    companion object {
        private const val FOO_REGION_ID = "123123"
        private val QUAD_0: QuadKey<LonLat> = quad("0")
        private val QUAD_1: QuadKey<LonLat> = quad("1")
        private val QUAD_2: QuadKey<LonLat> = quad("2")
        private const val REGION_ID = "foo"
        private const val FOO_REGION_ENTITY_NAME = "region_foo"
        val EMITTED_FRAGMENT_COMPONENTS = listOf(
            FragmentComponent::class,
            WorldDimensionComponent::class,
            WorldOriginComponent::class,
            ScreenLoopComponent::class,
            ScaleComponent::class,
            ParentLayerComponent::class
        )
    }
}
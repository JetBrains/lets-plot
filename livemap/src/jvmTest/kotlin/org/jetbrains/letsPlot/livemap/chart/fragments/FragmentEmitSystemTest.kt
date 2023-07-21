/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragments

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.livemap.Mocks
import org.jetbrains.letsPlot.livemap.chart.fragments.FragmentSpec.Companion.quads
import org.jetbrains.letsPlot.livemap.Utils
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.chart.fragment.*
import org.jetbrains.letsPlot.livemap.config.createMapProjection
import org.jetbrains.letsPlot.livemap.core.Projections
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import org.jetbrains.letsPlot.livemap.core.layers.ParentLayerComponent
import org.jetbrains.letsPlot.livemap.core.multitasking.SchedulerSystem
import org.jetbrains.letsPlot.livemap.geocoding.RegionIdComponent
import org.jetbrains.letsPlot.livemap.mapengine.LayerEntitiesComponent
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraInputSystem
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldDimensionComponent
import org.jetbrains.letsPlot.livemap.mapengine.placement.WorldOriginComponent
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridStateComponent
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FragmentEmitSystemTest : RegionsTestBase() {
    private lateinit var parentLayerEntity: EcsEntity
    private lateinit var fragmentFoo0: FragmentSpec
    private lateinit var fragmentFoo1: FragmentSpec
    private lateinit var emptyFragmentFoo2: FragmentSpec
    override val systemsOrder
        get() = listOf(
            CameraInputSystem::class,
            FragmentEmitSystem::class,
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
        addSystem(FragmentEmitSystem(Int.MAX_VALUE, componentManager))
        Mockito.`when`(liveMapContext.mapProjection).thenReturn(createMapProjection(Projections.mercator()))
        fragmentFoo0 = FragmentSpec(FOO_REGION_ID, QUAD_0).setGeometries(Utils.square(1, 2, 30, 40))
        fragmentFoo1 = FragmentSpec(FOO_REGION_ID, QUAD_1).setGeometries(Utils.square(5, 6, 70, 80))
        emptyFragmentFoo2 = FragmentSpec(FOO_REGION_ID, QUAD_2).setGeometries(Utils.empty())
    }

    @Test
    fun whenNothingToAddShouldDoNothing() {
        update()
    }

    @Test
    fun fragmentComponentsShouldContainCorrectValues() {
        update(
            Mocks.camera(this).position(org.jetbrains.letsPlot.livemap.WorldPoint(0, 0)).zoom(1.0),
            Mocks.downloadingFragments(this).downloaded(fragmentFoo0, fragmentFoo1),
            Mocks.viewportGrid(this).visibleQuads(quads(fragmentFoo0, fragmentFoo1)),
            Mocks.changedFragments(this).requested(fragmentFoo0, fragmentFoo1)
        )
        update(
            waitGeometries()
        )

        run {
            val fragmentEntity: EcsEntity = getEntity(fragmentFoo0.name())
            Assertions.assertThat(fragmentEntity.contains(EMITTED_FRAGMENT_COMPONENTS)).isTrue
            Assertions.assertThat(fragmentEntity.get<WorldOriginComponent>().origin)
                .isEqualTo(org.jetbrains.letsPlot.livemap.WorldPoint(x = 128.7111111111111, y = 95.03156113339311))
            Assertions.assertThat(fragmentEntity.get<WorldDimensionComponent>().dimension)
                .isEqualTo(org.jetbrains.letsPlot.livemap.WorldPoint(x = 21.333333333333314, y = 31.545927733930668))
            Assertions.assertThat(fragmentEntity.get<FragmentComponent>().fragmentKey.quadKey)
                .isEqualTo(Utils.quad<LonLat>("0"))
            Assertions.assertThat(fragmentEntity.get<ParentLayerComponent>().layerId).isEqualTo(parentLayerEntity.id)
        }
        run {
            val fragmentEntity: EcsEntity = getEntity(fragmentFoo1.name())
            Assertions.assertThat(fragmentEntity.contains(EMITTED_FRAGMENT_COMPONENTS)).isTrue
            Assertions.assertThat(fragmentEntity.get<WorldOriginComponent>().origin)
                .isEqualTo(
                    org.jetbrains.letsPlot.livemap.WorldPoint(
                        x = 131.55555555555554,
                        y = 5.4495652533470364E-11
                    )
                )
            Assertions.assertThat(fragmentEntity.get<WorldDimensionComponent>().dimension)
                .isEqualTo(org.jetbrains.letsPlot.livemap.WorldPoint(x = 49.7777777777778, y = 123.72551367976955))
            Assertions.assertThat(fragmentEntity.get<FragmentComponent>().fragmentKey.quadKey)
                .isEqualTo(Utils.quad<LonLat>("1"))
            Assertions.assertThat(fragmentEntity.get<ParentLayerComponent>().layerId).isEqualTo(parentLayerEntity.id)
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
            .doesNotHaveWorldGeometry()
            .isNotEmitted
            .isNotReady
        update(waitGeometries())

        // Fragment entity created, but still transforming world geometry to screen
        assertThatFragment(fragmentFoo0)
            .haveEntity()
            .haveWorldGeometry()
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
        private val QUAD_0: QuadKey<LonLat> = Utils.quad("0")
        private val QUAD_1: QuadKey<LonLat> = Utils.quad("1")
        private val QUAD_2: QuadKey<LonLat> = Utils.quad("2")
        private const val REGION_ID = "foo"
        private const val FOO_REGION_ENTITY_NAME = "region_foo"
        val EMITTED_FRAGMENT_COMPONENTS = listOf(
            FragmentComponent::class,
            WorldDimensionComponent::class,
            WorldOriginComponent::class,
            ParentLayerComponent::class
        )
    }
}
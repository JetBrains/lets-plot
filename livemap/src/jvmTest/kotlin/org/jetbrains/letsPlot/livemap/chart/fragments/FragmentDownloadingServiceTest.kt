/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart.fragments

import org.jetbrains.letsPlot.commons.intern.async.SimpleAsync
import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.Mocks
import org.jetbrains.letsPlot.livemap.Utils
import org.jetbrains.letsPlot.gis.geoprotocol.Fragment
import org.jetbrains.letsPlot.livemap.chart.fragment.*
import org.jetbrains.letsPlot.livemap.core.ecs.EcsSystem
import org.jetbrains.letsPlot.livemap.core.multitasking.SchedulerSystem
import org.jetbrains.letsPlot.livemap.mapengine.camera.CameraComponent
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*
import java.util.stream.Collectors
import kotlin.reflect.KClass

class FragmentDownloadingServiceTest : org.jetbrains.letsPlot.livemap.LiveMapTestBase() {
    private lateinit var myFragmentProvider: FragmentProvider
    private lateinit var fragmentFoo0: FragmentSpec
    private fun assertRequestsCount(count: Int, vararg specs: FragmentSpec) {
        val request = getFragmentRequestParams(listOf(*specs))
        Mockito.verify(myFragmentProvider, Mockito.times(count)).getFragments(request)
    }

    @Before
    override fun setUp() {
        super.setUp()
        myFragmentProvider = Mockito.mock(FragmentProvider::class.java)
        addSystem(FragmentDownloadingSystem(999, myFragmentProvider, componentManager))
        createEntity(
            "FragmentsChange",
            ChangedFragmentsComponent(),
            EmptyFragmentsComponent(),
            StreamingFragmentsComponent(),
            CachedFragmentsComponent()
        )
        createEntity("Camera", CameraComponent(myCamera))
        fragmentFoo0 = FragmentSpec(FOO_REGION_ID, QUAD_0).setGeometries(Utils.square(1, 2, 30, 40))
    }

    override val systemsOrder: List<KClass<out EcsSystem>>
        get() = listOf(
            FragmentDownloadingSystem::class,
            SchedulerSystem::class
        )

    @Test
    fun alreadyStreamingFragmentsShouldBeRequested() {
        configTileGeometryResponse(fragmentFoo0) // no success - waiting for response
        update(Mocks.changedFragments(this).requested(fragmentFoo0))
        update(Mocks.changedFragments(this).requested(fragmentFoo0))
        // assertRequestsCount(1, fragmentFoo0); // TODO: fix it later
    }

    private fun configTileGeometryResponse(vararg queries: FragmentSpec): Mocks.FragmentsResponseAsync {
        return configTileGeometryResponse(Arrays.asList(*queries))
    }

    private fun configTileGeometryResponse(queries: List<FragmentSpec>): Mocks.FragmentsResponseAsync {
        val responses: MutableMap<String, MutableList<Fragment>> = HashMap<String, MutableList<Fragment>>()
        queries.forEach { q: FragmentSpec ->
            if (!responses.containsKey(q.regionId())) {
                responses[q.regionId()] = ArrayList<Fragment>()
            }
            responses[q.regionId()]!!.add(Fragment(q.quad(), listOf(q.geometries())))
        }
        val async: SimpleAsync<Map<String, List<Fragment>>> = SimpleAsync()
        val request = getFragmentRequestParams(queries)
        Mockito.`when`(myFragmentProvider.getFragments(request)).thenReturn(async)
        return Mocks.FragmentsResponseAsync(async, responses)
    }

    private fun getFragmentRequestParams(queries: List<FragmentSpec>): HashMap<String, MutableSet<QuadKey<LonLat>>> {

        val ids = queries.stream().map(FragmentSpec::regionId).distinct().collect(Collectors.toList())
        val quads = queries.stream().map(FragmentSpec::quad)
            .distinct().collect(Collectors.toSet())

        val regionRequest = HashMap<String, MutableSet<QuadKey<LonLat>>>()

        for (id in ids) {
            regionRequest[id] = HashSet(quads)
        }

        return regionRequest
    }

    companion object {
        private const val FOO_REGION_ID = "123123"
        private val QUAD_0: QuadKey<LonLat> = QuadKey("0")
    }
}
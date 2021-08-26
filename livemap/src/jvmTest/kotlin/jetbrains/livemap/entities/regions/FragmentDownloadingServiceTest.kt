package jetbrains.datalore.maps.livemap.entities.regions

import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.spatial.LonLat
import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.datalore.jetbrains.livemap.Mocks
import jetbrains.datalore.jetbrains.livemap.Mocks.FragmentsResponseAsync
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.datalore.maps.Utils.square
import jetbrains.gis.geoprotocol.Fragment
import jetbrains.livemap.camera.CameraComponent
import jetbrains.livemap.core.ecs.EcsSystem
import jetbrains.livemap.core.multitasking.SchedulerSystem
import jetbrains.livemap.regions.*
import jetbrains.livemap.services.FragmentProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.reflect.KClass

class FragmentDownloadingServiceTest : LiveMapTestBase() {
    private lateinit var myFragmentProvider: FragmentProvider
    private lateinit var fragmentFoo0: FragmentSpec
    private fun assertRequestsCount(count: Int, vararg specs: FragmentSpec) {
        val (first, second) = getFragmentRequestParams(listOf(*specs))
        Mockito.verify(myFragmentProvider, Mockito.times(count)).getFragments(first, second)
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
        fragmentFoo0 = FragmentSpec(FOO_REGION_ID, QUAD_0).setGeometries(square(1, 2, 30, 40))
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

    private fun configTileGeometryResponse(vararg queries: FragmentSpec): FragmentsResponseAsync {
        return configTileGeometryResponse(Arrays.asList(*queries))
    }

    private fun configTileGeometryResponse(queries: List<FragmentSpec>): FragmentsResponseAsync {
        val responses: MutableMap<String, MutableList<Fragment>> = HashMap<String, MutableList<Fragment>>()
        queries.forEach(Consumer { q: FragmentSpec ->
            if (!responses.containsKey(q.regionId())) {
                responses[q.regionId()] = ArrayList<Fragment>()
            }
            responses[q.regionId()]!!.add(Fragment(q.quad(), listOf(q.geometries())))
        })
        val async: SimpleAsync<Map<String, List<Fragment>>> = SimpleAsync<Map<String, List<Fragment>>>()
        val (first, second) = getFragmentRequestParams(queries)
        Mockito.`when`(myFragmentProvider.getFragments(first, second)).thenReturn(async)
        return FragmentsResponseAsync(async, responses)
    }

    private fun getFragmentRequestParams(queries: List<FragmentSpec>): Pair<List<String>, Set<QuadKey<LonLat>>> {
        val ids = queries.stream().map { obj: FragmentSpec -> obj.regionId() }.distinct().collect(Collectors.toList())
        val quads = queries.stream().map { obj: FragmentSpec -> obj.quad() }
            .distinct().collect(Collectors.toSet())
        return Pair(ids, quads)
    }

    companion object {
        private const val FOO_REGION_ID = "123123"
        private val QUAD_0: QuadKey<LonLat> = QuadKey("0")
    }
}
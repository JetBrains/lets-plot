/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap

import jetbrains.datalore.base.async.SimpleAsync
import jetbrains.datalore.base.typedGeometry.MultiPolygon
import jetbrains.datalore.base.typedGeometry.Untyped
import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase.*
import jetbrains.datalore.jetbrains.livemap.entities.regions.FragmentSpec
import jetbrains.datalore.jetbrains.livemap.tile.Mocks.ViewportGridSpec
import jetbrains.gis.geoprotocol.Fragment
import jetbrains.livemap.World
import jetbrains.livemap.WorldPoint
import jetbrains.livemap.core.ecs.EcsEntity
import jetbrains.livemap.fragment.*
import java.util.*


object Mocks {
    fun viewportGrid(testBase: LiveMapTestBase): ViewportGridSpec {
        return ViewportGridSpec(testBase)
    }

    fun changedFragments(testBase: LiveMapTestBase): ChangedFragmentsSpec {
        return ChangedFragmentsSpec(testBase)
    }

    fun emittedFragments(testBase: LiveMapTestBase): EmittedFragmentsSpec {
        return EmittedFragmentsSpec(testBase)
    }

    fun cachedFragments(testBase: LiveMapTestBase): CachedFragmentsSpec {
        return CachedFragmentsSpec(testBase)
    }

    fun emptyFragments(testBase: LiveMapTestBase): EmptyFragmentsSpec {
        return EmptyFragmentsSpec(testBase)
    }

    fun downloadingFragments(testBase: LiveMapTestBase): DownloadingFragmentsSpec {
        return DownloadingFragmentsSpec(testBase)
    }

    fun camera(testBase: LiveMapTestBase): CameraSpec {
        return CameraSpec(testBase)
    }

    fun scheduler(testBase: LiveMapTestBase): SchedulerSpec {
        return testBase.schedulerSpec()
    }

    fun repeatUpdate(testBase: LiveMapTestBase): RepeatSpec {
        return testBase.repeatSpec()
    }

    fun cameraUpdate(testBase: LiveMapTestBase?): CameraUpdateSpec {
        return CameraUpdateSpec(testBase)
    }

    class ChangedFragmentsSpec internal constructor(testBase: LiveMapTestBase) : MockSpec(testBase) {

        private var myRequested = emptyList<FragmentKey>()
        private var myObsolete = emptyList<FragmentKey>()

        override fun apply() {
            val changedFragmentsComponent =
                componentManager.getSingleton<ChangedFragmentsComponent>()
            changedFragmentsComponent.setToAdd(myRequested)
            changedFragmentsComponent.setToRemove(myObsolete)
        }

        fun requested(vararg fragmentSpecs: FragmentSpec): ChangedFragmentsSpec {
            myRequested = listOf(*fragmentSpecs).map(FragmentSpec::key)
            return this
        }

        fun obsolete(vararg fragmentSpecs: FragmentSpec): ChangedFragmentsSpec {
            myObsolete = listOf(*fragmentSpecs).map(FragmentSpec::key)
            return this
        }

        fun none(): ChangedFragmentsSpec {
            myRequested = emptyList()
            myObsolete = emptyList()
            return this
        }
    }

    class EmptyFragmentsSpec(testBase: LiveMapTestBase) : MockSpec(testBase) {
        private val myEmptyFragments = HashSet<FragmentKey>()

        fun add(vararg specs: FragmentSpec): EmptyFragmentsSpec {
            for (spec in specs) {
                myEmptyFragments.add(spec.key())
            }
            return this
        }

        override fun apply() {
            val component = componentManager.getSingleton<EmptyFragmentsComponent>()
            component.addAll(myEmptyFragments)
        }
    }

    class CachedFragmentsSpec internal constructor(testBase: LiveMapTestBase) : MockSpec(testBase) {
        private val myAddedFragments = HashMap<FragmentKey, EcsEntity>()
        private val myRemovedFragments = HashSet<FragmentKey>()

        override fun apply() {
            val cachedFragmentsComponent = componentManager.getSingleton<CachedFragmentsComponent>()
            myAddedFragments.forEach(cachedFragmentsComponent::store)
            myRemovedFragments.forEach(cachedFragmentsComponent::dispose)
        }

        fun add(vararg specs: FragmentSpec): CachedFragmentsSpec {
            for (spec in specs) {
                myAddedFragments[spec.key()] = spec.readyEntity()!!
            }
            return this
        }

        fun drop(vararg specs: FragmentSpec): CachedFragmentsSpec {
            for (spec in specs) {
                myRemovedFragments.add(spec.key())
            }
            return this
        }

        fun none(): MockSpec {
            myRemovedFragments.addAll(componentManager.getSingleton<CachedFragmentsComponent>().keys())
            return this
        }
    }

    class EmittedFragmentsSpec internal constructor(testBase: LiveMapTestBase) : MockSpec(testBase) {
        private val myReceivedFragments = HashSet<FragmentKey>()

        fun add(vararg fragmentSpecs: FragmentSpec): EmittedFragmentsSpec {
            Arrays.stream(fragmentSpecs).forEach { spec -> myReceivedFragments.add(spec.key()) }

            return this
        }

        fun none(): EmittedFragmentsSpec {
            myReceivedFragments.clear()
            return this
        }

        override fun apply() {
            val emittedFragmentsComponent =
                componentManager.getSingleton<EmittedFragmentsComponent>()
            emittedFragmentsComponent.setEmitted(myReceivedFragments)
        }
    }

    class CameraUpdateSpec internal constructor(testBase: LiveMapTestBase?) : MockSpec(testBase!!) {
        private var myNone = false
        private var myIntegerZoomChanged = false
        private var myFractionZoomChanged = false
        private var myZoom: Optional<Double> = Optional.empty()
        private var myIntegerZoom: Optional<Int> = Optional.empty()
        fun zoom(zoom: Double): CameraUpdateSpec {
            myZoom = Optional.of(zoom)
            myIntegerZoom = Optional.of(zoom.toInt())
            myIntegerZoomChanged = false
            myFractionZoomChanged = true
            return this
        }

        fun zoom(zoom: Int): CameraUpdateSpec {
            myZoom = Optional.of(zoom.toDouble())
            myIntegerZoom = Optional.of(zoom)
            myIntegerZoomChanged = true
            myFractionZoomChanged = true
            return this
        }

        fun kind(zoomKinds: EnumSet<ZoomKind>): CameraUpdateSpec {
            myIntegerZoomChanged = zoomKinds.contains(ZoomKind.INTEGER)
            myFractionZoomChanged = zoomKinds.contains(ZoomKind.FRACTION)
            return this
        }

        fun none(): CameraUpdateSpec {
            myNone = true
            return this
        }

        override fun apply() {
            //val component: CameraUpdateComponent = componentManager.getSingleton<CameraUpdateComponent>()
            if (myNone) {
                myNone = false
            //    component.nothing()
            } else {
                myZoom.ifPresent { liveMapContext.camera.requestZoom(it) }
                myIntegerZoom.ifPresent { liveMapContext.camera.requestZoom(it.toDouble()) }
                //component.setIntegerZoomChanged(myIntegerZoomChanged)
                //component.setFractionZoomChanged(myFractionZoomChanged)
            }
        }

        enum class ZoomKind {
            INTEGER, FRACTION
        }
    }

    class CameraSpec internal constructor(testBase: LiveMapTestBase) : MockSpec(testBase) {
        private var myPosition: Vec<World>? = null
        private var myZoom: Double? = null

        fun zoom(zoom: Double): CameraSpec {
            myZoom = zoom
            return this
        }

        fun position(p: WorldPoint): CameraSpec {
            myPosition = p
            return this
        }

        override fun apply() {
            myZoom?.let { liveMapContext.camera.requestZoom(it); myZoom = null }
            myPosition?.let { liveMapContext.camera.requestPosition(it); myPosition = null }
        }
    }

    class DownloadingFragmentsSpec(testBase: LiveMapTestBase) : MockSpec(testBase) {
        private var myDownloaded: MutableMap<FragmentKey, MultiPolygon<Untyped>> = HashMap()

        fun downloaded(vararg downloaded: FragmentSpec): DownloadingFragmentsSpec {
            val r = HashMap<FragmentKey, MultiPolygon<Untyped>>()
            for (fragmentSpec in downloaded) {
                r[fragmentSpec.key()] = fragmentSpec.geometries().asMultipolygon()
            }
            myDownloaded = r
            return this
        }

        override fun apply() {
            val component = componentManager.getSingleton<DownloadingFragmentsComponent>()
            component.downloaded = myDownloaded
        }

        fun none(): DownloadingFragmentsSpec {
            myDownloaded = HashMap()
            return this
        }
    }

    class FragmentsResponseAsync(
        private val myAsync: SimpleAsync<Map<String, List<Fragment>>>,
        private val myResponse: Map<String, List<Fragment>>
    ) {

        internal fun success() {
            myAsync.success(myResponse)
        }
    }

}
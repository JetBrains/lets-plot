/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:Suppress("unused")

package org.jetbrains.letsPlot.livemap

import org.jetbrains.letsPlot.commons.intern.async.SimpleAsync
import org.jetbrains.letsPlot.commons.intern.typedGeometry.MultiPolygon
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Untyped
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.livemap.LiveMapTestBase.*
import org.jetbrains.letsPlot.livemap.chart.fragments.FragmentSpec
import org.jetbrains.letsPlot.livemap.tile.Mocks.ViewportGridSpec
import org.jetbrains.letsPlot.gis.geoprotocol.Fragment
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.WorldPoint
import org.jetbrains.letsPlot.livemap.chart.fragment.*
import org.jetbrains.letsPlot.livemap.core.ecs.EcsEntity
import java.util.*


object Mocks {
    fun viewportGrid(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): ViewportGridSpec {
        return ViewportGridSpec(testBase)
    }

    fun changedFragments(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): ChangedFragmentsSpec {
        return ChangedFragmentsSpec(testBase)
    }

    fun emittedFragments(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): EmittedFragmentsSpec {
        return EmittedFragmentsSpec(testBase)
    }

    fun cachedFragments(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): CachedFragmentsSpec {
        return CachedFragmentsSpec(testBase)
    }

    fun emptyFragments(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): EmptyFragmentsSpec {
        return EmptyFragmentsSpec(testBase)
    }

    fun downloadingFragments(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): DownloadingFragmentsSpec {
        return DownloadingFragmentsSpec(testBase)
    }

    fun camera(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): CameraSpec {
        return CameraSpec(testBase)
    }

    fun scheduler(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): SchedulerSpec {
        return testBase.schedulerSpec()
    }

    fun repeatUpdate(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase): RepeatSpec {
        return testBase.repeatSpec()
    }

    fun cameraUpdate(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase?): CameraUpdateSpec {
        return CameraUpdateSpec(testBase)
    }

    class ChangedFragmentsSpec internal constructor(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {

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

    class EmptyFragmentsSpec(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {
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

    @Suppress("unused")
    class CachedFragmentsSpec internal constructor(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {
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

    class EmittedFragmentsSpec internal constructor(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {
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

    class CameraUpdateSpec internal constructor(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase?) : MockSpec(testBase!!) {
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
                myZoom.ifPresent(liveMapContext.camera::requestZoom)
                myIntegerZoom.ifPresent { liveMapContext.camera.requestZoom(it.toDouble()) }
                //component.setIntegerZoomChanged(myIntegerZoomChanged)
                //component.setFractionZoomChanged(myFractionZoomChanged)
            }
        }

        enum class ZoomKind {
            INTEGER, FRACTION
        }
    }

    class CameraSpec internal constructor(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {
        private var myPosition: Vec<org.jetbrains.letsPlot.livemap.World>? = null
        private var myZoom: Double? = null

        fun zoom(zoom: Double): CameraSpec {
            myZoom = zoom
            return this
        }

        fun position(p: org.jetbrains.letsPlot.livemap.WorldPoint): CameraSpec {
            myPosition = p
            return this
        }

        override fun apply() {
            myZoom?.let { liveMapContext.camera.requestZoom(it); myZoom = null }
            myPosition?.let { liveMapContext.camera.requestPosition(it); myPosition = null }
        }
    }

    class DownloadingFragmentsSpec(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : MockSpec(testBase) {
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
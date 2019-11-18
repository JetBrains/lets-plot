/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.concurrent.Lock
import jetbrains.datalore.base.concurrent.execute
import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.geoprotocol.GeoTile
import jetbrains.livemap.LiveMapContext
import jetbrains.livemap.LiveMapSystem
import jetbrains.livemap.core.Utils.diff
import jetbrains.livemap.core.ecs.EcsComponentManager
import jetbrains.livemap.fragments.FragmentProvider

class FragmentDownloadingSystem(
    private val myMaxActiveDownloads: Int,
    private val myFragmentGeometryProvider: FragmentProvider,
    componentManager: EcsComponentManager
) : LiveMapSystem(componentManager) {
    private val myRegionTiles = HashMap<String, MutableList<GeoTile>>()
    private val myLock = Lock()

    override fun initImpl(context: LiveMapContext) {
        createEntity("DownloadingFragments").addComponent(DownloadingFragmentsComponent())
    }

    override fun updateImpl(context: LiveMapContext, dt: Double) {
        val downloadingFragments = getSingleton<DownloadingFragmentsComponent>()
        val changedFragments = getSingleton<ChangedFragmentsComponent>()
        val streamingFragments = getSingleton<StreamingFragmentsComponent>()
        val cachedFragments = getSingleton<CachedFragmentsComponent>()

        downloadingFragments.reduceQueue(changedFragments.obsolete)
        downloadingFragments.extendQueue(
            Utils.SetBuilder.ofCopy(changedFragments.requested)
                .exclude(streamingFragments.keys())
                .exclude(cachedFragments.keys())
                .exclude(downloadingFragments.downloading)
                .get()
        )

        run {
            // download fragments if there are any empty downloading slots
            if (downloadingFragments.downloading.size < myMaxActiveDownloads) {
                val zoomQueue = downloadingFragments.getZoomQueue(camera().zoom.toInt())
                val availableDownloadsCount = myMaxActiveDownloads - downloadingFragments.downloading.size
                val toDownload = zoomQueue.take(availableDownloadsCount)
                if (toDownload.isNotEmpty()) {
                    streamingFragments.addAll(toDownload)
                    downloadingFragments.reduceQueue(toDownload)
                    downloadingFragments.extendDownloading(toDownload)

                    downloadGeometries(toDownload)
                }
            }
        }

        val downloadedFragments = HashMap<FragmentKey, MultiPolygon<Generic>>()
        run {
            // process downloadedFragments fragments
            var responses = emptyMap<String, List<GeoTile>>()
            myLock.execute {
                if (!myRegionTiles.isEmpty()) {
                    responses = HashMap<String, List<GeoTile>>(myRegionTiles)
                    myRegionTiles.clear()
                }
            }

            responses.forEach { (regionId, fragmentsData) ->
                for (fragmentData in fragmentsData) {
                    val fragmentKey = FragmentKey(regionId, fragmentData.key)
                    downloadedFragments[fragmentKey] = fragmentData.multiPolygon
                }
            }
        }

        downloadingFragments.reduceDownloading(downloadedFragments.keys)
        downloadingFragments.downloaded = downloadedFragments
    }

    private fun <T> MutableCollection<T>.take(n: Int): Set<T> {
        var number = n
        if (isEmpty() || number < 1) {
            return emptySet()
        }

        val res = HashSet<T>(number)
        val iter = iterator()

        while (iter.hasNext() && number >= 0) {
            res.add(iter.next())
            iter.remove()
            number--
        }

        return res
    }

    private fun downloadGeometries(fragmentsToFetch: Collection<FragmentKey>) {
        val regionRequest = HashMap<String, MutableSet<QuadKey>>()
        val fetchingFragments = getSingleton<StreamingFragmentsComponent>()

        for (newFragment in fragmentsToFetch) {
            regionRequest.getOrPut(newFragment.regionId, ::HashSet).add(newFragment.quadKey)
            fetchingFragments.add(newFragment)
        }

        regionRequest.forEach { (requestRegionId, requestQuads) ->
            myFragmentGeometryProvider
                .getGeometries(listOf(requestRegionId), requestQuads)
                .onSuccess { receivedTiles ->
                    receivedTiles.forEach { (regionId, geoTiles) ->
                        val registeredTiles = ArrayList(geoTiles)

                        // Emulate response for empty quads - this is needed to finish waiting for a fragment data
                        val receivedQuads = geoTiles.map { it.key }.toSet()

                        diff(requestQuads, receivedQuads).forEach { emptyQuad ->
                            registeredTiles.add(
                                GeoTile(
                                    emptyQuad,
                                    emptyList()
                                )
                            )
                        }

                        myLock.execute {
                            myRegionTiles
                                .getOrPut(regionId, ::ArrayList)
                                .addAll(registeredTiles)

                            return@execute
                        }
                    }
                }
        }
    }
}
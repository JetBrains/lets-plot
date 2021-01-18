/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.livemap

import jetbrains.datalore.base.async.Async
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.cursorTooltip
import jetbrains.livemap.LiveMap

class LiveMapTargetLocator(
    liveMapAsync: Async<LiveMap>,
    private val myTargetSource: Map<Pair<Int, Int>, ContextualMapping>
) : GeomTargetLocator {
    private var myLiveMap: LiveMap? = null

    init {
        liveMapAsync.map { myLiveMap = it }
    }

    override fun search(coord: DoubleVector): GeomTargetLocator.LookupResult? {
       return myLiveMap
           ?.search(coord)
           ?.let {
               GeomTargetLocator.LookupResult(
                   targets = listOf(
                       GeomTarget(
                           hitIndex = it.index,
                           tipLayoutHint = cursorTooltip(coord, it.color),
                           aesTipLayoutHints = emptyMap()
                       )
                   ),
                   distance = 0.0, // livemap shows tooltip only on hover
                   geomKind = GeomKind.LIVE_MAP,
                   contextualMapping = myTargetSource[it.layerIndex to it.index] ?: error("Can't find target."),
                   isCrosshairEnabled = false
           )
       }
    }
}
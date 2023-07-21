/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.tile

import org.jetbrains.letsPlot.commons.intern.spatial.LonLat
import org.jetbrains.letsPlot.commons.intern.spatial.QuadKey
import org.jetbrains.letsPlot.livemap.LiveMapTestBase
import org.jetbrains.letsPlot.livemap.mapengine.viewport.ViewportGridStateComponent
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object Mocks {
    class ViewportGridSpec(testBase: org.jetbrains.letsPlot.livemap.LiveMapTestBase) : org.jetbrains.letsPlot.livemap.LiveMapTestBase.MockSpec(testBase) {
        private var myToAdd = emptySet<QuadKey<LonLat>>()
        private var myToRemove = emptySet<QuadKey<LonLat>>()
        private var myVisibleQuads: MutableMap<QuadKey<LonLat>, Int> = HashMap()

        fun quadsToAdd(vararg ts: QuadKey<LonLat>): ViewportGridSpec {
            myToAdd = HashSet<QuadKey<LonLat>>(listOf(*ts))
            return this
        }

        fun quadsToRemove(vararg ts: QuadKey<LonLat>): ViewportGridSpec {
            myToRemove = HashSet<QuadKey<LonLat>>(listOf(*ts))
            return this
        }

        fun visibleQuads(vararg quads: QuadKey<LonLat>): ViewportGridSpec {
            visibleQuads(listOf(*quads))
            return this
        }

        fun visibleQuads(quads: Iterable<QuadKey<LonLat>>): ViewportGridSpec {
            myVisibleQuads = HashMap()
            for (quad in quads) {
                myVisibleQuads[quad] = 1
            }
            return this
        }

        override fun apply() {
            val component = componentManager.getSingleton<ViewportGridStateComponent>()
            component.quadsToLoad = myToAdd
            component.quadsToRemove = myToRemove
            myVisibleQuads.forEach { (key, counter) -> component.quadsRefCounter[key] = counter }
        }
    }
}
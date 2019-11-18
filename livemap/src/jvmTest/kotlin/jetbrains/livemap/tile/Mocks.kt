/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.jetbrains.livemap.tile

import jetbrains.datalore.base.spatial.QuadKey
import jetbrains.datalore.jetbrains.livemap.LiveMapTestBase
import jetbrains.livemap.tiles.components.CellStateComponent
import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

object Mocks {
    fun cellState(testBase: LiveMapTestBase): CellStateSpec {
        return CellStateSpec(testBase)
    }

    class CellStateSpec(testBase: LiveMapTestBase) : LiveMapTestBase.MockSpec(testBase) {
        private var myToAdd = emptySet<QuadKey>()
        private var myToRemove = emptySet<QuadKey>()
        private var myVisibleQuads: MutableMap<QuadKey, Int> = HashMap()

        fun quadsToAdd(vararg ts: QuadKey): CellStateSpec {
            myToAdd = HashSet<QuadKey>(listOf(*ts))
            return this
        }

        fun quadsToRemove(vararg ts: QuadKey): CellStateSpec {
            myToRemove = HashSet<QuadKey>(listOf(*ts))
            return this
        }

        fun visibleQuads(vararg quads: QuadKey): CellStateSpec {
            myVisibleQuads = HashMap()
            for (quad in quads) {
                myVisibleQuads[quad] = 1
            }
            return this
        }

        override fun apply() {
            val component = componentManager.getSingleton<CellStateComponent>()
            component.quadsToAdd = myToAdd
            component.quadsToRemove = myToRemove
            myVisibleQuads.forEach { (key, counter) -> component.quadsRefCounter[key] = counter }
        }
    }
}
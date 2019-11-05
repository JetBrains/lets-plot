/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.ui

import jetbrains.datalore.vis.canvas.Canvas
import jetbrains.datalore.vis.canvas.CanvasProvider

class ResourceManager(private val canvasProvider: CanvasProvider) {
    private val snapshotMap: MutableMap<String, Canvas.Snapshot> = HashMap()

    fun add(key: String, dataUrl: String): ResourceManager {
        canvasProvider.createSnapshot(dataUrl).onResult(
            { snapshot -> snapshotMap[key] = snapshot },
            { message -> error(message) })
        return this
    }

    operator fun get(key: String): Canvas.Snapshot {
        return snapshotMap[key]!!
    }

    fun isReady(vararg keys: String): Boolean = isReady(listOf(*keys))

    private fun isReady(keys: Collection<String>): Boolean {
        return keys.all { snapshotMap.containsKey(it) }
    }
}
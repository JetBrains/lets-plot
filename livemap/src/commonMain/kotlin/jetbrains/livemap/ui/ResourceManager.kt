package jetbrains.livemap.ui

import jetbrains.datalore.visualization.base.canvas.Canvas
import jetbrains.datalore.visualization.base.canvas.CanvasProvider

class ResourceManager(private val canvasProvider: CanvasProvider) {
    private val snapshotMap: MutableMap<String, Canvas.Snapshot> = HashMap()

    fun add(key: String, base64Image: String): ResourceManager {
        canvasProvider.createSnapshot(base64Image).onResult(
            { snapshot -> snapshotMap[key] = snapshot },
            { message -> error(message) })
        return this
    }

    operator fun get(key: String): Canvas.Snapshot {
        return snapshotMap[key]!!
    }

    fun isReady(vararg keys: String): Boolean = isReady(listOf(*keys))

    private fun isReady(keys: Collection<String>): Boolean {

        keys.forEach {
            if (!snapshotMap.containsKey(it)) {
                return false
            }
        }

        return true
    }
}
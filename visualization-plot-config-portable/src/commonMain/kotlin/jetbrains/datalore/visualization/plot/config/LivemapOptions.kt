package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.visualization.plot.base.geom.LivemapGeom.*

class LivemapOptions internal constructor(
        val zoom: Int?,
        val location: Any?,
        val stroke: Double?,
        val interactive: Boolean,
        val magnifier: Boolean,
        val displayMode: DisplayMode,
        val featureLevel: String?,
        val parent: Any?,
        val scaled: Boolean,
        val clustering: Boolean,
        val labels: Boolean,
        val theme: Theme,
        val projection: Projection,
        val geodesic: Boolean,
        val devParams: Map<*, *>)

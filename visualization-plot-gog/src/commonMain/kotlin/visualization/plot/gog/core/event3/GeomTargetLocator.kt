package jetbrains.datalore.visualization.plot.gog.core.event3

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.core.GeomKind

interface GeomTargetLocator {

    fun findTargets(coord: DoubleVector): LocatedTargets?

    enum class LookupSpace {
        X, XY, NONE
    }

    enum class LookupStrategy {
        HOVER, NEAREST, NONE
    }

    open class LocatedTargets(
            val geomTargets: List<GeomTarget>,
            private val myDistance: Double,
            open val geomKind: GeomKind,
            val contextualMapping: ContextualMapping) {

        open val distance: Double
            get() {
                checkArgument(!isEmpty)
                return myDistance
            }

        val isEmpty: Boolean
            get() = geomTargets.isEmpty()
    }

    class LookupSpec(val lookupSpace: LookupSpace, val lookupStrategy: LookupStrategy)
}

package jetbrains.datalore.visualization.plot.base.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.base.GeomKind

interface GeomTargetLocator {

    fun search(coord: DoubleVector): LookupResult?

    enum class LookupSpace {
        X, XY, NONE
    }

    enum class LookupStrategy {
        HOVER, NEAREST, NONE
    }

    class LookupSpec(val lookupSpace: LookupSpace, val lookupStrategy: LookupStrategy) {
        companion object {
            val NONE = LookupSpec(LookupSpace.NONE, LookupStrategy.NONE)
        }
    }

    // `open` for Mockito test
    open class LookupResult(
            val targets: List<GeomTarget>,
            open val distance: Double,
            open val geomKind: GeomKind,
            val contextualMapping: ContextualMapping)
}

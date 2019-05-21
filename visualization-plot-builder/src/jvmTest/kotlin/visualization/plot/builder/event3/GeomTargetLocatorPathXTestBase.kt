package jetbrains.datalore.visualization.plot.builder.event3

import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupSpace
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetLocator.LookupStrategy
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.PathPoint
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.PathPointsBuilder
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.createLocator
import jetbrains.datalore.visualization.plot.builder.event3.TestUtil.pathTarget
import kotlin.test.BeforeTest

abstract class GeomTargetLocatorPathXTestBase {

    internal lateinit var p0: PathPoint
    internal lateinit var p1: PathPoint
    internal lateinit var p2: PathPoint
    internal lateinit var locator: GeomTargetLocator

    internal abstract val strategy: LookupStrategy

    @BeforeTest
    fun setUp() {
        val builder = PathPointsBuilder().defaultY(Y)
        p0 = builder.x(100.0)
        p1 = builder.x(200.0)
        p2 = builder.x(300.0)

        locator = createLocator(LookupStrategy.HOVER, LookupSpace.X,
                pathTarget(FIRST_PATH_KEY, p0, p1, p2)
        )
    }

    companion object {
        internal const val FIRST_PATH_KEY = 1
        internal const val Y = 17.0
        internal const val THIS_POINT_DISTANCE = 25.0
        internal const val MIDDLE_POINTS_DISTANCE = 50.0
        internal const val NEXT_POINT_DISTANCE = 60.0
    }

}

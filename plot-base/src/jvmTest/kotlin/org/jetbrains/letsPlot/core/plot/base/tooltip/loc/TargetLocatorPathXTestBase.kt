/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.loc

import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.base.tooltip.TestUtil
import kotlin.test.BeforeTest

abstract class TargetLocatorPathXTestBase {

    internal lateinit var p0: TestUtil.PathPoint
    internal lateinit var p1: TestUtil.PathPoint
    internal lateinit var p2: TestUtil.PathPoint
    internal lateinit var locator: GeomTargetLocator

    internal abstract val strategy: LookupStrategy

    @BeforeTest
    fun setUp() {
        val builder = TestUtil.PathPointsBuilder().defaultY(Y)
        p0 = builder.x(100.0)
        p1 = builder.x(200.0)
        p2 = builder.x(300.0)

        locator = TestUtil.createLocator(
            LookupStrategy.HOVER, LookupSpace.X,
            TestUtil.pathTarget(FIRST_PATH_KEY, p0, p1, p2)
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

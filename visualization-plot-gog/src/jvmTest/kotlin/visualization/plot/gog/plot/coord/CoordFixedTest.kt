package jetbrains.datalore.visualization.plot.gog.plot.coord

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class CoordFixedTest : CoordTestBase() {

    @BeforeTest
    fun setUp() {
        dataBounds = DoubleRectangle(DoubleVector.ZERO, DATA_SPAN)
    }

    @Test
    fun adjustDomains() {
        // fixed ratio == 1 (equal X and Y)
        val dataBounds = dataBounds!!
        val rangeX = dataBounds.xRange()
        val rangeY = dataBounds.yRange()

        tryAdjustDomains(2.0, PROVIDER_EQUAL_XY, rangeX, expand(rangeY, 2.0))
        tryAdjustDomains(0.5, PROVIDER_EQUAL_XY, expand(rangeX, 2.0), rangeY)

        // stretched Y
        run {
            // two ratios compensate
            val ratio = 2.0
            tryAdjustDomains(ratio, PROVIDER_2x_Y, rangeX, rangeY)
        }
        run {
            // two ratios multiply
            val ratio = .5
            val expectedX = expand(rangeX, 2.0 * (1 / ratio))  // coord system (2) + display (2)
            tryAdjustDomains(ratio, PROVIDER_2x_Y, expectedX, rangeY)
        }

        // stretched X
        run {
            // two ratios multiply
            val ratio = 2.0
            val expectedY = expand(rangeX, 2.0 * ratio)  // coord system (2) + display (2)
            tryAdjustDomains(ratio, PROVIDER_2x_X, rangeX, expectedY)
        }
        run {
            // two ratios multiply
            val ratio = .5
            val expectedX = expand(rangeX, 2.0 * (1 / ratio)) // coord system (2) + display (2)
            tryAdjustDomains(ratio, PROVIDER_2x_Y, expectedX, rangeY)
        }
    }

    @Test
    fun applyScales() {
        // Square grid fit into the display
        tryApplyScales(2.0, PROVIDER_EQUAL_XY, DoubleVector(1.0, 1.0))
        tryApplyScales(.5, PROVIDER_EQUAL_XY, DoubleVector(1.0, 1.0))

        tryApplyScales(2.0, PROVIDER_2x_Y, DoubleVector(1.0, 2.0))
        tryApplyScales(.5, PROVIDER_2x_Y, DoubleVector(.5, 1.0))

        tryApplyScales(2.0, PROVIDER_2x_X, DoubleVector(1.0, .5))
        tryApplyScales(.5, PROVIDER_2x_X, DoubleVector(2.0, 1.0))
    }

    private fun tryApplyScales(ratio: Double, provider: CoordProvider, multiplier: DoubleVector) {
        val shortSide = shortSideOfDisplay(ratio)
        tryApplyScales(ratio, provider,
                DoubleVector(0.0, 0.0),
                DoubleVector(shortSide * multiplier.x, shortSide * multiplier.y),
                DoubleVector.ZERO)
    }

    private fun shortSideOfDisplay(ratio: Double): Double {
        val displaySize = unitDisplaySize(ratio)
        return Math.min(displaySize.x, displaySize.y)
    }

    companion object {
        private val PROVIDER_EQUAL_XY = CoordProviders.fixed(1.0)
        private val PROVIDER_2x_Y = CoordProviders.fixed(2.0)
        private val PROVIDER_2x_X = CoordProviders.fixed(0.5)

        private val DATA_SPAN = DoubleVector(10.0, 10.0)
    }
}
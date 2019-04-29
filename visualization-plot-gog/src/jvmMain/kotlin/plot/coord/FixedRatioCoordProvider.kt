package jetbrains.datalore.visualization.plot.gog.plot.coord

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Pair
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil

internal open class FixedRatioCoordProvider
/**
 * A fixed scale coordinate system forces a specified ratio between the physical representation of data units on the axes.
 * The ratio represents the number of units on the y-axis equivalent to one unit on the x-axis.
 * ratio = 1, ensures that one unit on the x-axis is the same length as one unit on the y-axis.
 * Ratios higher than one make units on the y axis longer than units on the x-axis, and vice versa.
 */
(private val myRatio: Double) : CoordProviderBase() {

    override fun adjustDomains(xDomain: ClosedRange<Double>, yDomain: ClosedRange<Double>, displaySize: DoubleVector): Pair<ClosedRange<Double>, ClosedRange<Double>> {
        var xDomain = xDomain
        var yDomain = yDomain
        val spanX = SeriesUtil.span(xDomain)
        val spanY = SeriesUtil.span(yDomain)
        if (spanX < SeriesUtil.TINY || spanY < SeriesUtil.TINY) {
            return Pair(xDomain, yDomain) // don't touch
        }

        // fit the data into the display
        var displayW = displaySize.x
        var displayH = displaySize.y

        // Distort display size to account for 'ratio'
        // ratio == 1 -> X-units equal Y-units
        // ratio > 1 -> Y-units are longer
        // ratio < 1 -> X-units are longer
        if (myRatio > 1) {
            displayW *= myRatio
        } else {
            displayH *= 1 / myRatio
        }

        val ratioX = spanX / displayW
        val ratioY = spanY / displayH

        // Take bigger ratio and apply to ortogonal domain (axis) so that
        // ratio: (data range) / (axis length) is the same for both X and Y.
        if (ratioX > ratioY) {
            val spanAdjusted = displayH * ratioX
            yDomain = SeriesUtil.expand(yDomain, spanAdjusted)
        } else {
            val spanAdjusted = displayW * ratioY
            xDomain = SeriesUtil.expand(xDomain, spanAdjusted)
        }

        return Pair(xDomain, yDomain)
    }
}

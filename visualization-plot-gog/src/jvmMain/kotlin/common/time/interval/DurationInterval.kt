package jetbrains.datalore.visualization.plot.gog.common.time.interval

import jetbrains.datalore.base.datetime.Duration

internal class DurationInterval(private val myDuration: Duration, count: Int) : TimeInterval(count) {

    override// milliseconds
    // fractional seconds
    // seconds
    // minutes
    // hours
    // days
    // weeks
    val tickFormatPattern: String
        get() {
            val duration = myDuration.duration
            if (duration < Duration.SECOND.duration) {
                return "S"
            } else if (duration < Duration.MINUTE.duration) {
                return "ss"
            } else if (duration < Duration.HOUR.duration) {
                return "mm"
            } else if (duration < Duration.DAY.duration) {
                return "H:mm"
            } else if (duration < Duration.WEEK.duration) {
                return "MMM d"
            }
            return "MMM d"
        }

    init {
        if (!myDuration.isPositive) {
            throw RuntimeException("Duration must be positive")
        }
    }

    override fun range(start: Double, end: Double): List<Double> {
        val step = (myDuration.duration * count).toDouble()
        var tick = Math.ceil(start / step) * step
        val result = ArrayList<Double>()
        while (tick <= end) {
            result.add(tick)
            tick += step
        }
        return result
    }
}

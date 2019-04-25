package jetbrains.datalore.base.datetime

class Duration(val duration: Long) : Comparable<Duration> {

    val isPositive: Boolean
        get() = duration > 0

    fun mul(times: Long): Duration {
        return Duration(duration * times)
    }

    fun add(duration: Duration): Duration {
        return Duration(this.duration + duration.duration)
    }

    fun sub(duration: Duration): Duration {
        return Duration(this.duration - duration.duration)
    }

    operator fun div(duration: Duration): Double {
        return this.duration / duration.duration.toDouble()
    }

    override fun compareTo(o: Duration): Int {
        val delta = duration - o.duration
        return if (delta > 0) {
            1
        } else if (delta == 0L) {
            0
        } else {
            -1
        }
    }

    override fun hashCode(): Int {
        return duration.toInt()
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj !is Duration) false else duration == obj.duration

    }

    override fun toString(): String {
        return "Duration : " + duration + "ms"
    }

    companion object {
        val MS = Duration(1)
        val SECOND = MS.mul(1000)
        val MINUTE = SECOND.mul(60)
        val HOUR = MINUTE.mul(60)
        val DAY = HOUR.mul(24)
        val WEEK = DAY.mul(7)
    }
}

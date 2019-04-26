package jetbrains.datalore.base.datetime

class Time @JvmOverloads constructor(val hours: Int, val minutes: Int, val seconds: Int = 0, val milliseconds: Int = 0) : Comparable<Time> {

    init {
        if (hours < 0 || hours > 24) {
            throw IllegalArgumentException()
        }
        if (hours == 24 && (minutes != 0 || seconds != 0)) {
            throw IllegalArgumentException()
        }
        if (minutes < 0 || minutes >= 60) {
            throw IllegalArgumentException()
        }
        if (seconds < 0 || seconds >= 60) {
            throw IllegalArgumentException()
        }
    }

    override fun compareTo(other: Time): Int {
        var delta = hours - other.hours
        if (delta != 0) return delta
        delta = minutes - other.minutes
        if (delta != 0) return delta
        delta = seconds - other.seconds
        return if (delta != 0) delta else milliseconds - other.milliseconds
    }

    override fun hashCode(): Int {
        return hours * 239 + minutes * 491 + seconds * 41 + milliseconds
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Time) false else compareTo((other as Time?)!!) == 0

    }

    override fun toString(): String {
        val result = StringBuilder()
        if (hours < 10) result.append("0")
        result.append(hours)
        if (minutes < 10) result.append("0")
        result.append(minutes)
        if (seconds < 10) result.append("0")
        result.append(seconds)
        return result.toString()
    }

    fun toPrettyHMString(): String {
        val result = StringBuilder()
        if (hours < 10) result.append("0")
        result.append(hours).append(DELIMITER)
        if (minutes < 10) result.append("0")
        result.append(minutes)
        return result.toString()
    }

    companion object {
        private val DELIMITER = ':'
        val DAY_START = Time(0, 0)
        val DAY_END = Time(24, 0)

        fun parse(s: String): Time {
            if (s.length < 6) {
                throw IllegalArgumentException()
            }

            val hours = Integer.parseInt(s.substring(0, 2))
            val minutes = Integer.parseInt(s.substring(2, 4))
            val seconds = Integer.parseInt(s.substring(4, 6))

            return Time(hours, minutes, seconds)
        }

        fun fromPrettyHMString(time: String): Time {
            if (!time.contains(DELIMITER + "")) {
                throw IllegalArgumentException()
            }
            val length = time.length
            if (length != 5 && length != 4) {
                throw IllegalArgumentException()
            }
            val hourLength = if (length == 4) 1 else 2
            try {
                return Time(Integer.parseInt(time.substring(0, hourLength)), Integer.parseInt(time.substring(hourLength + 1, length)), 0)
            } catch (ignored: NumberFormatException) {
                throw IllegalArgumentException()
            }

        }
    }

}

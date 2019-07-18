package jetbrains.datalore.base.numberFormat

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.round

internal class Format(pattern: String) {

    private val matchResult = patternRegex.find(pattern) ?: throw IllegalArgumentException("Wrong pattern format")

    private var fill = matchResult.groups[1]?.value ?: " "
    private var align = matchResult.groups[2]?.value ?: ">"
    private val sign = matchResult.groups[3]?.value ?: "-"
    private val symbol = matchResult.groups[4]?.value
    private var zero = matchResult.groups[5] != null
    private val width = (matchResult.groups[6]?.value ?: "-1").toInt()
    private val comma = matchResult.groups[7] != null
    private var precision = (matchResult.groups[8]?.value ?: "-1").toInt()
    private var type = matchResult.groups[9]?.value ?: ""

    fun apply(num: Number): String {
        if (type == "") {
            if (precision == -1) precision = 12
            type = "g"
        }

        if (zero || (fill == "0" && align == "=")) {
            zero = true
            fill = "0"
            align = "="
        }

        var prefix = computePrefix()
        var suffix = computeSuffix()

        val n: Double = num.toDouble()
        var str: String

        if (type == "c") {
            suffix = "${applyType(n)}$suffix"
            str = ""
        } else {
            var isNegative = n < 0.0
            str = applyType(abs(n))

            if (isNegative && str.toDouble() == 0.0) isNegative = false

            prefix = (if (isNegative) {
                "-"
            } else {
                if (sign != "-") sign else ""
            }) + prefix
        }

        if (comma && !zero) str = group(str, Int.MAX_VALUE)

        val length = prefix.length + str.length + suffix.length
        var padding = if (length < width) fill.repeat(width - length) else ""

        if (comma && zero) {
            str = group(
                padding + str,
                if (padding.isNotEmpty()) width - suffix.length else Int.MAX_VALUE
            )
            padding = ""
        }

        str = when (align) {
            "<" -> "$prefix$str$suffix$padding"
            "=" -> "$prefix$padding$str$suffix"
            "^" -> {
                val stop = padding.length shr 1
                "${padding.slice(0 until stop)}$prefix$str$suffix${padding.slice(stop until padding.length)}"

            }
            else -> "$padding$prefix$str$suffix"
        }

        return str
    }

    private fun applyType(num: Double) = when (type) {
        "%" -> toFixed(num * 100, precision)
        "b" -> round(num).toLong().toString(2)
        "c" -> num.toString()
        "d" -> round(num).toLong().toString(10)
        "e" -> toExponential(num, precision)
        "f" -> toFixed(num, precision)
        "g" -> toPrecision(num, precision)
        "o" -> round(num).toLong().toString(8)
        "X" -> round(num).toLong().toString(16).toUpperCase()
        "x" -> round(num).toLong().toString(16)
        else -> throw IllegalArgumentException("Wrong type: $type")
    }

    private fun computePrefix(): String =
        when (symbol) {
            "$" -> CURRENCY
            "#" -> if ("boxX".indexOf(type) > -1) "0${type.toLowerCase()}" else ""
            else -> ""
        }

    private fun computeSuffix(): String =
        if (type == "%") PERCENT else ""

    private fun group(str: String, width: Int): String {
        var i = str.length
        val list = mutableListOf<String>()
        var length = 0
        var g = 3

        while (i > 0 && g > 0) {
            if (length + g + 1 > width) g = max(1, width - length)
            i -= g
            if (i < 0) {
                g += i
                i = 0
            }
            list.add(str.substring(i until i + g))
            length += g + 1
            if (length > width) break
        }

        list.reverse()

        return list.joinToString(COMMA)
    }

    companion object {
        private val patternRegex =
            """^(?:([^{}])?([<>=^]))?([+ -])?(#|$)?(0)?(\d+)?(,)?(?:\.(\d+))?([%bcdefgosXx])?$""".toRegex()
        private const val CURRENCY = "$"
        private const val PERCENT = "%"
        private const val COMMA = ","

        internal fun toPrecision(num: Double, precision: Int = -1): String {
            var n = num
            if (precision == -1) {
                if (n == 0.0) {
                    return "0"
                }
                return num.toString()
            }

            n = roundToPrecision(n, precision)

            val iPart = n.toLong()

            return if (iPart == 0L) {
                var p = precision
                if (n == 0.0) {
                    --p
                    if (n.toString()[0] == '-') {
                        n = -n
                    }
                }
                toFixed(n, p)
            } else {
                val iPartLen = iPart.toString().length
                if (iPartLen > precision) {
                    toExponential(num, precision - 1)
                } else {
                    toFixed(num, precision - iPartLen)
                }
            }
        }

        internal fun toFixed(num: Double, precision: Int = -1): String {
            if (precision == -1 || precision == 0) return round(num).toLong().toString()

            var n = num
            if (n == 0.0 && n.toString()[0] == '-') {
                n = -n
            }

            val str = roundToPrecision(n, precision).toString()

            val arr = str.split('.')
            val fraction = if (arr.size == 1) "0".repeat(precision) else arr[1].padEnd(precision, '0')
            return "${arr[0]}.$fraction"
        }

        internal fun toExponential(num: Double, precision: Int = -1): String {
            if (num == 0.0) {
                return "${toFixed(num, precision)}e+0"
            }

            var n = num
            val arr = n.toString().split('.')
            var ePow: Int
            var sign: Char
            if (arr[0] == "0") {
                ePow = 0
                while (arr[1][ePow] == '0') {
                    ++ePow
                }
                ++ePow
                repeat(ePow) { n *= 10.0 }
                sign = '-'
                var str = n.toString()
                str = str.slice(0 until arr[1].length - ePow + 2)
                n = str.toDouble()
            } else {
                ePow = arr[0].length - 1
                repeat(ePow) { n /= 10 }
                sign = '+'
            }

            var nStr = if (precision == -1) {
                n.toString()
            } else {
                n = roundToPrecision(n, precision)
                if (n >= 10) {
                    n /= 10
                    --ePow
                    if (ePow == 0) {
                        sign = '+'
                    }
                }
                toFixed(n, precision)
            }

            val resDouble = nStr.toDouble()

            if (resDouble % 1 == 0.0 && precision == -1) {
                nStr = resDouble.toInt().toString()
            }

            return "${nStr}e$sign$ePow"
        }

        private fun roundToPrecision(num: Double, precision: Int): Double {
            var mul = 1.0
            repeat(precision) { mul *= 10 }
            return round(num * mul) / mul
        }
    }
}
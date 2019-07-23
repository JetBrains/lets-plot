package jetbrains.datalore.base.numberFormat

import kotlin.math.*

internal class Format(private val spec: Spec) {

    constructor(spec: String) : this(create(spec))

    data class Spec(
        val fill: String = " ",
        val align: String = ">",
        val sign: String = "-",
        val symbol: String,
        val zero: Boolean,
        val width: Int = -1,
        val comma: Boolean,
        val precision: Int = -1,
        val type: String = ""
    ) {
        val typeSet
                get() = type != ""
        val precisionSet
                get() = precision != -1
    }

    data class NumberInfo(
        val number: Double,
        val negative: Boolean,
        val length: Int,
        val commaIndex: Int,
        val integerLength: Int,
        val fractionLength: Int,
        val integerPart: Long,
        val fractionPart: Long
    )

    data class Result(
        val numberInfo: NumberInfo,
        val typedString: String = "",
        val prefix: String = "",
        val suffix: String = "",
        val padding: String = "",
        val resultString: String = ""
    )


    fun apply(num: Number): String {
        val numberInfo = createNumberInfo(num)
        var result = Result(numberInfo)

        result = applyType(result)
        result = computePrefix(result)
        result = computeSuffix(result)

        if (spec.comma && !spec.zero) {
            result = group(result)
        }

        result = computePadding(result)

        if (spec.comma && spec.zero) {
            result = group(
                result,
                if (result.padding.isNotEmpty()) spec.width - result.suffix.length else Int.MAX_VALUE
            )
        }

        result = applyAllign(result)

        return result.resultString
    }

    private fun applyAllign(res: Result): Result {
        val resultString = when (spec.align) {
            "<" -> "${res.prefix}${res.typedString}${res.suffix}${res.padding}"
            "=" -> "${res.prefix}${res.padding}${res.typedString}${res.suffix}"
            "^" -> {
                val stop = res.padding.length / 2
                "${res.padding.slice(0 until stop)}${res.prefix}${res.typedString}${res.suffix}${res.padding.slice(stop until res.padding.length)}"

            }
            else -> "${res.padding}${res.prefix}${res.typedString}${res.suffix}"
        }

        return res.copy(resultString = resultString)
    }

    private fun applyType(res: Result): Result {
        val num = res.numberInfo.number.absoluteValue
        val typedString = when (spec.type) {
            "%" -> toFixed(num * 100, spec.precision)
            "b" -> round(num).toLong().toString(2)
            "c" -> num.toString()
            "d" -> round(num).toLong().toString(10)
            "e" -> toExponential(num, spec.precision)
            "f" -> toFixed(num, spec.precision)
            "g" -> toPrecision(num, spec.precision)
            "o" -> round(num).toLong().toString(8)
            "X" -> round(num).toLong().toString(16).toUpperCase()
            "x" -> round(num).toLong().toString(16)
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(typedString = typedString)
    }

    private fun computePrefix(res: Result): Result {
        val prefix = when (spec.symbol) {
            "$" -> CURRENCY
            "#" -> if ("boxX".indexOf(spec.type) > -1) "0${spec.type.toLowerCase()}" else ""
            else -> ""
        }
        val isNegative = res.numberInfo.number.sign < 0 && res.typedString.toDouble() != 0.0
        val signStr = if (isNegative) {
            "-"
        } else {
            if (spec.sign != "-") spec.sign else ""
        }
        return res.copy(prefix = signStr + prefix)
    }

    private fun computeSuffix(res: Result): Result {
        val suffix = if (spec.type == "%") PERCENT else ""
        return res.copy(suffix = suffix)
    }

    private fun computePadding(res: Result): Result {
        val length = res.prefix.length + res.typedString.length + res.suffix.length
        val padding = if (length < spec.width) spec.fill.repeat(spec.width - length) else ""
        return res.copy(padding = padding)
    }

    private fun group(res: Result, width: Int = Int.MAX_VALUE): Result {
        val str = res.typedString
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

        return res.copy(typedString = list.joinToString(COMMA))
    }

    companion object {
        private const val CURRENCY = "$"
        private const val PERCENT = "%"
        private const val COMMA = ","

        fun create(spec: String): Spec {
            return create(parse(spec))
        }

        fun create(spec: Spec): Spec {
            var precision = spec.precision
            var type = spec.type
            if (type == "") {
                if (precision == -1) {
                    precision = 12
                }
                type = "g"
            }


            var zero = spec.zero
            var fill = spec.fill
            var align = spec.align
            if (zero || (fill == "0" && align == "=")) {
                zero = true
                fill = "0"
                align = "="
            }

            return spec.copy(type = type, precision = precision, zero = zero, fill = fill, align = align)
        }

        fun parse(spec: String): Spec {
            val patternRegex =
                """^(?:([^{}])?([<>=^]))?([+ -])?([#$])?(0)?(\d+)?(,)?(?:\.(\d+))?([%bcdefgosXx])?$""".toRegex()
            val matchResult = patternRegex.find(spec) ?: throw IllegalArgumentException("Wrong pattern format")

            return Spec(
                fill = matchResult.groups[1]?.value ?: " ",
                align = matchResult.groups[2]?.value ?: ">",
                sign = matchResult.groups[3]?.value ?: "-",
                symbol = matchResult.groups[4]?.value ?: "",
                zero = matchResult.groups[5] != null,
                width = (matchResult.groups[6]?.value ?: "-1").toInt(),
                comma = matchResult.groups[7] != null,
                precision = (matchResult.groups[8]?.value ?: "-1").toInt(),
                type = matchResult.groups[9]?.value ?: ""
            )
        }

        internal fun createNumberInfo(num: Number): NumberInfo {
            val number = num.toDouble()
            val negative = number < 0.0
            val numberString = number.toString()
            val length = numberString.length
            val commaIndex = numberString.indexOf('.')

            val integerPart = number.toLong()

            val fractionPart = ((number - integerPart) * 10.0.pow(16)).toLong()

            val integerLength = integerPart.toString().length
            val fractionLength = if (fractionPart > 0) fractionPart.toString().length else 0

            return NumberInfo(
                number,
                negative,
                length,
                commaIndex,
                integerLength,
                fractionLength,
                integerPart,
                fractionPart
            )
        }

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
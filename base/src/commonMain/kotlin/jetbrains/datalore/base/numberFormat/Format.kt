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
    )

    data class NumberInfo(
        val number: Double,
        val negative: Boolean,
        val integerPart: Long,
        val fractionPart: Long,
        val exponent: Int?
    ) {

        override fun toString(): String {
            val sign = if (negative) "-" else ""
            val integerString = integerPart.toString()
            val exponentString = if (exponent != null) {
                val expSign = if (exponent.sign >= 0) "+" else ""
                "e$expSign$exponent"
            } else {
                ""
            }
            return "$sign$integerString.$fractionString$exponentString"
        }

        val fractionString: String
            get() {
                val fractionString = fractionPart.toString()
                val fractionPrefix = "0".repeat(FRACTION_LENGTH - fractionString.length)
                return fractionPrefix + fractionString.replace("0+$".toRegex(), "")
            }

        val integerLength: Int
            get() = integerPart.toString().length

        val fractionInDouble: Double
            get() = fractionPart.toDouble() / 10.0.pow(FRACTION_LENGTH)

        companion object {
            const val FRACTION_LENGTH = 15
        }
    }

    data class NumberParts(
        val body: String = "",
        val prefix: String = "",
        val suffix: String = "",
        val padding: String = ""
    )


    fun apply(num: Number): String {
        val numberInfo = createNumberInfo(num)
        var parts = NumberParts()

        parts = applyType(parts, numberInfo)
        parts = computePrefix(parts, numberInfo)
        parts = computeSuffix(parts)

        if (spec.comma && !spec.zero) {
            parts = applyGroup(parts)
        }

        parts = computePadding(parts)

        if (spec.comma && spec.zero) {
            val padding = parts.padding
            parts = parts.copy(body = padding + parts.body, padding = "")
            parts = applyGroup(parts)
        }

        return getAlignedString(parts)
    }

    private fun getAlignedString(res: NumberParts) = when (spec.align) {
        "<" -> "${res.prefix}${res.body}${res.suffix}${res.padding}"
        "=" -> "${res.prefix}${res.padding}${res.body}${res.suffix}"
        "^" -> {
            val stop = res.padding.length / 2
            "${res.padding.slice(0 until stop)}${res.prefix}${res.body}${res.suffix}${res.padding.slice(stop until res.padding.length)}"

        }
        else -> "${res.padding}${res.prefix}${res.body}${res.suffix}"
    }

    private fun applyGroup(res: NumberParts): NumberParts {
        val str = res.body
        val strList = str.split('.')
        val intStr = group(strList[0])
        val fractionStr = if (strList.size > 1) ".${strList[1]}" else ""
        return res.copy(body = "$intStr$fractionStr")
    }

    private fun applyType(res: NumberParts, numberInfo: NumberInfo): NumberParts {
        val num = numberInfo.number.absoluteValue
        val typedString = when (spec.type) {
            "%" -> {
                val percentNumberInfo = createNumberInfo(num * 100)
                toFixedString(percentNumberInfo, spec.precision)
            }
            "b" -> round(num).toLong().toString(2)
            "c" -> num.toString()
            "d" -> round(num).toLong().toString(10)
            "e" -> toExponential(numberInfo, spec.precision).toString()
            "f" -> toFixedString(numberInfo, spec.precision)
            "g" -> toPrecision(numberInfo, spec.precision).toString()
            "o" -> round(num).toLong().toString(8)
            "X" -> round(num).toLong().toString(16).toUpperCase()
            "x" -> round(num).toLong().toString(16)
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(body = typedString)
    }

    private fun computePrefix(
        res: NumberParts,
        numberInfo: NumberInfo
    ): NumberParts {
        val prefix = when (spec.symbol) {
            "$" -> CURRENCY
            "#" -> if ("boxX".indexOf(spec.type) > -1) "0${spec.type.toLowerCase()}" else ""
            else -> ""
        }
        val isNegative = numberInfo.number.sign < 0 && res.body.toDouble() != 0.0
        val signStr = if (isNegative) {
            "-"
        } else {
            if (spec.sign != "-") spec.sign else ""
        }
        return res.copy(prefix = signStr + prefix)
    }

    private fun computeSuffix(res: NumberParts): NumberParts {
        val suffix = if (spec.type == "%") PERCENT else ""
        return res.copy(suffix = suffix)
    }

    private fun computePadding(res: NumberParts): NumberParts {
        val length = res.prefix.length + res.body.length + res.suffix.length
        val padding = if (length < spec.width) spec.fill.repeat(spec.width - length) else ""
        return res.copy(padding = padding)
    }

    companion object {
        private const val CURRENCY = "$"
        private const val PERCENT = "%"
        private const val COMMA = ","
        private const val GROUP_SIZE = 3

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

            val integerPart = number.toLong()
            val fractionPart = ((number - integerPart) * 10.0.pow(NumberInfo.FRACTION_LENGTH)).toLong()

            return NumberInfo(
                number,
                negative,
                integerPart,
                fractionPart,
                null
            )
        }

        internal fun toPrecision(numberInfo: NumberInfo, precision: Int = -1): NumberInfo {
            var result = roundToPrecision(numberInfo, precision)

            if (result.integerPart != 0L && result.integerLength > precision) {
                result = toExponential(result)
            }

            return result
        }


        internal fun toExponential(numberInfo: NumberInfo, precision: Int = -1): NumberInfo {
            val num = numberInfo.number
            if (num == 0.0) {
                return numberInfo.copy(exponent = 0)
            }

            var newInfo = numberInfo

            if (precision > -1) {
                newInfo = roundToPrecision(numberInfo)
            }

            var e: Int
            var n: Double
            if (newInfo.integerPart == 0L) {
                e = 0
                n = newInfo.fractionInDouble
                do {
                    n *= 10
                    ++e
                } while (n < 0)
                e = -e
            } else {
                e = -(newInfo.integerLength - 1)
                n = num / 10.0.pow(e)
            }

            newInfo = createNumberInfo(n)

            return newInfo.copy(exponent = e)
        }

        internal fun toFixedString(numberInfo: NumberInfo, precision: Int = 0): String {
            if (precision == -1 || precision == 0) return round(numberInfo.number).toLong().toString()

            val newNumberInfo = roundToPrecision(numberInfo, precision)

            if (newNumberInfo.fractionPart == 0L) {
                return "${newNumberInfo.integerPart}.${"0".repeat(precision)}"
            }

            val fractionString = newNumberInfo.fractionString.padEnd(precision, '0')

            return "${newNumberInfo.integerPart}.$fractionString"
        }

        private fun roundToPrecision(numberInfo: NumberInfo, precision: Int = 0): NumberInfo {
            val fraction =
                ((numberInfo.fractionPart.toDouble() / 10.0.pow(NumberInfo.FRACTION_LENGTH - precision)).roundToLong() *
                        10.0.pow(NumberInfo.FRACTION_LENGTH - precision)).toLong()


            val num = numberInfo.fractionPart.toDouble() + fraction.toDouble() / 10.0.pow(NumberInfo.FRACTION_LENGTH)
            return numberInfo.copy(number = num, fractionPart = fraction)
        }

        private fun group(str: String) = str
            .reversed() // 1234 -> 4321
            .asSequence()
            .chunked(GROUP_SIZE) // [[4,3,2], [1]]
            .map { it.joinToString("") } // [[432], [1]]
            .joinToString(COMMA) // 432,1
            .reversed() // 1,234
    }
}
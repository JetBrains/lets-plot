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
        val precision: Int = 6,
        val type: String = "",
        val trim: Boolean = false
    )

    data class NumberInfo(
        val number: Double,
        val negative: Boolean,
        val integerPart: Long,
        val fractionPart: Long,
        val exponent: Int?
    ) {

        val fractionString: String
            get() {
                val fractionString = fractionPart.toString()
                val fractionPrefix = "0".repeat(fractionLeadingZeros)
                return fractionPrefix + fractionString.replace("0+$".toRegex(), "")
            }

        val fractionLeadingZeros: Int
            get() = if (fractionPart != 0L) FRACTION_LENGTH - floor(log10(fractionPart.toDouble())).toInt() - 1 else 1

        val integerLength: Int
            get() = integerPart.toString().length

        companion object {
            const val FRACTION_LENGTH = 19
        }
    }

    data class NumberParts(
        val body: FormattedNumber = FormattedNumber(),
        val sign: String = "",
        val prefix: String = "",
        val suffix: String = "",
        val padding: String = ""
    )

    data class FormattedNumber(
        val integerPart: String = "",
        val fractionPart: String = "",
        val exponentPart: String = ""
    ) {
        val fractionLength
            get() = if (fractionPart.isNotEmpty()) FRACTION_DELIMITER_LENGTH + fractionPart.length else 0

        val fullLength: Int
            get() = integerPart.length + fractionLength + exponentPart.length

        override fun toString() =
                "$integerPart${FRACTION_DELIMITER.takeIf { fractionPart.isNotEmpty() } ?: ""}$fractionPart$exponentPart"
    }


    fun apply(num: Number): String {
        val numberInfo = createNumberInfo(num)
        var parts = NumberParts()

        parts = computeBody(parts, numberInfo)
        parts = applyTrim(parts)
        parts = computeSign(parts, numberInfo)
        parts = computePrefix(parts)
        parts = computeSuffix(parts)

        if (spec.comma && !spec.zero) {
            parts = applyGroup(parts)
        }

        parts = computePadding(parts)

        if (spec.comma && spec.zero) {
            parts = applyGroup(parts)
        }

        return getAlignedString(parts)
    }

    private fun getAlignedString(parts: NumberParts): String {
        return when (spec.align) {
            "<" -> "${parts.sign}${parts.prefix}${parts.body}${parts.suffix}${parts.padding}"
            "=" -> "${parts.sign}${parts.prefix}${parts.padding}${parts.body}${parts.suffix}"
            "^" -> {
                val stop = parts.padding.length / 2
                "${parts.padding.slice(0 until stop)}${parts.sign}${parts.prefix}${parts.body}${parts.suffix}${parts.padding.slice(stop until parts.padding.length)}"

            }
            else -> "${parts.padding}${parts.sign}${parts.prefix}${parts.body}${parts.suffix}"
        }
    }

    private fun applyGroup(parts: NumberParts): NumberParts {
        val formattedNumber = parts.body

        val intStr = formattedNumber.integerPart
        val expStr = formattedNumber.exponentPart

        val zeroPadding = if (spec.zero) parts.padding else ""

        var fullIntStr = zeroPadding + intStr
        val valuableLen = intStr.length
        val fullLen = fullIntStr.length
        val commas = (ceil(fullLen.toDouble() / 3.0) - 1).toInt()

        val width = (spec.width - formattedNumber.fractionLength - expStr.length)
            .coerceAtLeast(valuableLen + commas)

        fullIntStr = group(fullIntStr)

        if (fullIntStr.length > width) {
            fullIntStr = fullIntStr.substring(fullIntStr.length - width)
            if (fullIntStr[0] == ',') {
                fullIntStr = "0$fullIntStr"
            }
        }

        val padding = if (spec.zero) "" else parts.padding

        return parts.copy(body = formattedNumber.copy(integerPart = fullIntStr), padding = padding)
    }

    private fun computeBody(res: NumberParts, numberInfo: NumberInfo): NumberParts {
        val num = numberInfo.number.absoluteValue
        val absoluteNumberInfo = createNumberInfo(num)
        val formattedNumber = when (spec.type) {
            "%" -> {
                val percentNumberInfo = createNumberInfo(num * 100)
                toFixedString(percentNumberInfo, spec.precision)
            }
            "b" -> FormattedNumber(round(num).toLong().toString(2))
            "c" -> FormattedNumber(num.toString())
            "d" -> toString(absoluteNumberInfo, 0)
            "e" -> toString(toExponential(absoluteNumberInfo, spec.precision), spec.precision)
            "f" -> toFixedString(absoluteNumberInfo, spec.precision)
            "g" -> toPrecisionString(absoluteNumberInfo, spec.precision)
            "o" -> FormattedNumber(round(num).toLong().toString(8))
            "X" -> FormattedNumber(round(num).toLong().toString(16).toUpperCase())
            "x" -> FormattedNumber(round(num).toLong().toString(16))
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(body = formattedNumber)
    }

    private fun applyTrim(res: NumberParts): NumberParts {
        val formattedNumber = res.body
        if (!spec.trim || formattedNumber.fractionPart.isEmpty()) return res

        val trimmedFraction = formattedNumber.fractionPart.replace("0+\$".toRegex(), "")
        return res.copy(body = formattedNumber.copy(fractionPart = trimmedFraction))
    }

    private fun computeSign(parts: NumberParts, numberInfo: NumberInfo): NumberParts {
        val isBodyZero = if ("boxX".indexOf(spec.type) == -1) {
            parts.body.integerPart.toLong() == 0L &&
                    (parts.body.fractionPart.isEmpty() || parts.body.fractionPart.toLong() == 0L)
        } else {
            parts.body.integerPart.toLong(16) == 0L
        }
        val isNegative = numberInfo.negative && !isBodyZero
        val signStr = if (isNegative) {
            "-"
        } else {
            if (spec.sign != "-") spec.sign else ""
        }
        return parts.copy(sign = signStr)
    }

    private fun computePrefix(parts: NumberParts): NumberParts {
        val prefix = when (spec.symbol) {
            "$" -> CURRENCY
            "#" -> if ("boxX".indexOf(spec.type) > -1) "0${spec.type.toLowerCase()}" else ""
            else -> ""
        }
        return parts.copy(prefix = prefix)
    }

    private fun computeSuffix(res: NumberParts): NumberParts {
        val suffix = if (spec.type == "%") PERCENT else ""
        return res.copy(suffix = suffix)
    }

    private fun computePadding(parts: NumberParts): NumberParts {
        val length = parts.sign.length + parts.prefix.length + parts.body.fullLength + parts.suffix.length
        val padding = if (length < spec.width) spec.fill.repeat(spec.width - length) else ""
        return parts.copy(padding = padding)
    }

    companion object {
        private const val CURRENCY = "$"
        private const val PERCENT = "%"
        private const val COMMA = ","
        private const val FRACTION_DELIMITER = "."
        private const val FRACTION_DELIMITER_LENGTH = FRACTION_DELIMITER.length
        private const val GROUP_SIZE = 3

        fun create(spec: String): Spec {
            return create(parse(spec))
        }

        fun create(spec: Spec): Spec {
            var precision = spec.precision
            var type = spec.type
            var trim = false
            if (type == "") {
                if (precision == -1) {
                    precision = 12
                }
                type = "g"
                trim = true
            }

            var zero = spec.zero
            var fill = spec.fill
            var align = spec.align
            if (zero || (fill == "0" && align == "=")) {
                zero = true
                fill = "0"
                align = "="
            }

            return spec.copy(type = type, precision = precision, zero = zero, fill = fill, align = align, trim = trim)
        }

        private fun parse(spec: String): Spec {
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
                precision = (matchResult.groups[8]?.value ?: "6").toInt(),
                type = matchResult.groups[9]?.value ?: ""
            )
        }

        private fun createNumberInfo(num: Number): NumberInfo {
            val number = num.toDouble()
            val negative = number < 0.0

            val exp = log10(number).toInt()

            return if (exp > NumberInfo.FRACTION_LENGTH) {
                val integerPart = (number / 10.0.pow(exp)).toLong()
                NumberInfo(
                    number,
                    negative,
                    integerPart,
                    0L,
                    exp
                )
            } else {
                val integerPart = number.toLong()
                val fractionPart = ((number - integerPart) * 10.0.pow(NumberInfo.FRACTION_LENGTH)).toLong().absoluteValue
                NumberInfo(
                    number,
                    negative,
                    integerPart,
                    fractionPart,
                    null
                )
            }
        }

        private fun toExponential(numberInfo: NumberInfo, precision: Int = -1): NumberInfo {
            val num = numberInfo.number
            if (num == 0.0) {
                return numberInfo.copy(exponent = 0)
            }

            var newInfo = numberInfo

            val e = if (newInfo.integerPart == 0L) {
                -(newInfo.fractionLeadingZeros + 1)
            } else {
                newInfo.integerLength - 1
            }
            val n = num / 10.0.pow(e)

            newInfo = createNumberInfo(n)

            if (precision > -1) {
                newInfo = roundToPrecision(newInfo, precision)
            }

            return newInfo.copy(exponent = e)
        }

        private fun toPrecisionString(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
            if (numberInfo.integerPart == 0L) {
                if (numberInfo.fractionPart == 0L) {
                    return toFixedString(numberInfo, precision - 1)
                }
                return toFixedString(numberInfo, precision + numberInfo.fractionLeadingZeros)
            } else {
                if (numberInfo.integerLength > precision) {
                    return toString(toExponential(numberInfo, precision - 1), precision - 1)
                }
                return toFixedString(numberInfo, precision - numberInfo.integerLength)
            }
        }


        private fun toFixedString(numberInfo: NumberInfo, precision: Int = 0): FormattedNumber {
            if (precision == -1 || precision == 0) return FormattedNumber(round(numberInfo.number).toLong().toString())

            val newNumberInfo = roundToPrecision(numberInfo, precision)

            if (newNumberInfo.fractionPart == 0L) {
                return FormattedNumber(newNumberInfo.integerPart.toString(), "0".repeat(precision))
            }

            val fractionString = newNumberInfo.fractionString.padEnd(precision, '0')

            return FormattedNumber(newNumberInfo.integerPart.toString(), fractionString)
        }

        private fun toString(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
            val exponentString = if (numberInfo.exponent != null) {
                val expSign = if (numberInfo.exponent.sign >= 0) "+" else ""
                "e$expSign${numberInfo.exponent}"
            } else {
                ""
            }

            val expNumberInfo = createNumberInfo(numberInfo.integerPart + numberInfo.fractionPart / 10.0.pow(NumberInfo.FRACTION_LENGTH))

            if (precision > -1) {
                val formattedNumber = toFixedString(expNumberInfo, precision)
                return formattedNumber.copy(exponentPart = exponentString)
            }


            val integerString = expNumberInfo.integerPart.toString()
            val fractionString = if (expNumberInfo.fractionPart == 0L) "" else expNumberInfo.fractionString
            return FormattedNumber(integerString, fractionString, exponentString)
        }

        private fun roundToPrecision(numberInfo: NumberInfo, precision: Int = 0): NumberInfo {
            var fraction =
                ((numberInfo.fractionPart.toDouble() / 10.0.pow(NumberInfo.FRACTION_LENGTH - precision)).roundToLong() *
                        10.0.pow(NumberInfo.FRACTION_LENGTH - precision)).toLong()
            if (fraction == 10.0.pow(NumberInfo.FRACTION_LENGTH).toLong()) {
                fraction = 0
            }

            val integerPart = (round(numberInfo.number * 10.0.pow(precision)) / 10.0.pow(precision)).toLong()

            val num = integerPart.toDouble() + fraction.toDouble() / 10.0.pow(NumberInfo.FRACTION_LENGTH)
            return numberInfo.copy(number = num, fractionPart = fraction, integerPart = integerPart)
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
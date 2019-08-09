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
            get() = if (fractionPart != 0L) MAX_SUPPORTED_FRACTION_EXP - floor(log10(fractionPart.toDouble())).toInt() - 1 else 1

        val integerLength: Int
            get() = integerPart.toString().length

        fun asPercent(): NumberInfo {
            return createNumberInfo(number * 100)
        }

        companion object {
            const val MAX_SUPPORTED_FRACTION_EXP = 19 // max fraction length we can format (as any other format library does)
            val MAX_SUPPORTED_FRACTION_VALUE = 10.0.pow(MAX_SUPPORTED_FRACTION_EXP)
        }
    }

    data class Output(
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
        var output = Output()

        output = computeBody(output, numberInfo)
        output = applyTrim(output)

        output = computeSign(output, numberInfo)
        output = computePrefix(output)
        output = computeSuffix(output)

        if (spec.comma && !spec.zero) {
            output = applyGroup(output)
        }

        output = computePadding(output)

        if (spec.comma && spec.zero) {
            output = applyGroup(output)
        }

        return getAlignedString(output)
    }

    private fun getAlignedString(output: Output): String {
        with(output) {
            return when (spec.align) {
                "<" -> "$sign$prefix$body$suffix$padding"
                "=" -> "$sign$prefix$padding$body$suffix"
                "^" -> {
                    val stop = padding.length / 2
                    "${padding.slice(0 until stop)}$sign$prefix$body$suffix${padding.slice(stop until output.padding.length)}"
                }
                else -> "$padding$sign$prefix$body$suffix"
            }
        }
    }

    private fun applyGroup(output: Output): Output {

        val zeroPadding = output.padding.takeIf { spec.zero } ?: ""

        val body = output.body
        var fullIntStr = zeroPadding + body.integerPart
        val commas = (ceil(fullIntStr.length / GROUP_SIZE.toDouble()) - 1).toInt()

        val width = (spec.width - body.fractionLength - body.exponentPart.length)
            .coerceAtLeast(body.integerPart.length + commas)

        fullIntStr = group(fullIntStr)

        if (fullIntStr.length > width) {
            fullIntStr = fullIntStr.substring(fullIntStr.length - width)
            if (fullIntStr.startsWith(',')) {
                fullIntStr = "0$fullIntStr"
            }
        }

        return output.copy(
            body = body.copy(integerPart = fullIntStr),
            padding = "".takeIf { spec.zero } ?: output.padding
        )
    }

    private fun computeBody(res: Output, numberInfo: NumberInfo): Output {
        val formattedNumber = when (spec.type) {
            "%" -> toFixedFormat(numberInfo.asPercent(), spec.precision)
            "c" -> FormattedNumber(numberInfo.number.toString())
            "d" -> toSimpleFormat(numberInfo, 0)
            "e" -> toSimpleFormat(toExponential(numberInfo, spec.precision), spec.precision)
            "f" -> toFixedFormat(numberInfo, spec.precision)
            "g" -> toPrecisionFormat(numberInfo, spec.precision)
            "b" -> FormattedNumber(numberInfo.number.roundToLong().toString(2))
            "o" -> FormattedNumber(numberInfo.number.roundToLong().toString(8))
            "X" -> FormattedNumber(numberInfo.number.roundToLong().toString(16).toUpperCase())
            "x" -> FormattedNumber(numberInfo.number.roundToLong().toString(16))
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(body = formattedNumber)
    }

    private fun applyTrim(output: Output): Output {
        if (!spec.trim || output.body.fractionPart.isEmpty()) {
            return output
        }

        val trimmedFraction = output.body.fractionPart.replace("0+\$".toRegex(), "")
        return output.copy(body = output.body.copy(fractionPart = trimmedFraction))
    }

    private fun computeSign(output: Output, numberInfo: NumberInfo): Output {
        val isBodyZero = output.body.run { (integerPart.asSequence() + fractionPart.asSequence()).all { it == '0' } }

        val isNegative = numberInfo.negative && !isBodyZero
        val signStr = if (isNegative) {
            "-"
        } else {
            if (spec.sign != "-") spec.sign else ""
        }
        return output.copy(sign = signStr)
    }

    private fun computePrefix(output: Output): Output {
        val prefix = when (spec.symbol) {
            "$" -> CURRENCY
            "#" -> if ("boxX".indexOf(spec.type) > -1) "0${spec.type.toLowerCase()}" else ""
            else -> ""
        }
        return output.copy(prefix = prefix)
    }

    private fun computeSuffix(res: Output): Output {
        val suffix = PERCENT.takeIf { spec.type == "%" }.orEmpty()
        return res.copy(suffix = suffix)
    }

    private fun computePadding(output: Output): Output {
        val length = output.sign.length + output.prefix.length + output.body.fullLength + output.suffix.length
        val padding = if (length < spec.width) spec.fill.repeat(spec.width - length) else ""
        return output.copy(padding = padding)
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
            val negative = num.toDouble() < 0.0
            val number = num.toDouble().absoluteValue
            val exp = log10(number).toInt()

            val integerPart: Long
            val fractionPart: Long
            val exponentPart: Int?

            if (exp > NumberInfo.MAX_SUPPORTED_FRACTION_EXP) { // Long number - to exponential form
                integerPart = (number / 10.0.pow(exp)).toLong()
                fractionPart = 0L // integer is too long - drop fraction
                exponentPart = exp
            } else {
                integerPart = number.toLong()
                fractionPart = ((number - integerPart) * NumberInfo.MAX_SUPPORTED_FRACTION_VALUE).toLong().absoluteValue
                exponentPart = null
            }

            return NumberInfo(
                number,
                negative,
                integerPart,
                fractionPart,
                exponentPart
            )
        }

        private fun toExponential(numberInfo: NumberInfo, precision: Int = -1): NumberInfo {
            val num = numberInfo.number
            if (num == 0.0) {
                return numberInfo.copy(exponent = 0)
            }

            val e = if (numberInfo.integerPart == 0L) {
                -(numberInfo.fractionLeadingZeros + 1)
            } else {
                numberInfo.integerLength - 1
            }
            val n = num / 10.0.pow(e)

            var newInfo = createNumberInfo(n)

            if (precision > -1) {
                newInfo = roundToPrecision(newInfo, precision)
            }

            return newInfo.copy(exponent = e)
        }

        private fun toPrecisionFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
            if (numberInfo.integerPart == 0L) {
                if (numberInfo.fractionPart == 0L) {
                    return toFixedFormat(numberInfo, precision - 1)
                }
                return toFixedFormat(numberInfo, precision + numberInfo.fractionLeadingZeros)
            } else {
                if (numberInfo.integerLength > precision) {
                    return toSimpleFormat(toExponential(numberInfo, precision - 1), precision - 1)
                }
                return toFixedFormat(numberInfo, precision - numberInfo.integerLength)
            }
        }

        private fun toFixedFormat(numberInfo: NumberInfo, precision: Int = 0): FormattedNumber {
            if (precision == -1 || precision == 0) {
                return FormattedNumber(numberInfo.number.roundToLong().toString())
            }

            val newNumberInfo = roundToPrecision(numberInfo, precision)

            if (newNumberInfo.fractionPart == 0L) {
                return FormattedNumber(newNumberInfo.integerPart.toString(), "0".repeat(precision))
            }

            val fractionString = newNumberInfo.fractionString.padEnd(precision, '0')

            return FormattedNumber(newNumberInfo.integerPart.toString(), fractionString)
        }

        private fun toSimpleFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
            val exponentString = if (numberInfo.exponent != null) {
                val expSign = if (numberInfo.exponent.sign >= 0) "+" else ""
                "e$expSign${numberInfo.exponent}"
            } else {
                ""
            }

            val expNumberInfo = createNumberInfo(numberInfo.integerPart + numberInfo.fractionPart / NumberInfo.MAX_SUPPORTED_FRACTION_VALUE)

            if (precision > -1) {
                val formattedNumber = toFixedFormat(expNumberInfo, precision)
                return formattedNumber.copy(exponentPart = exponentString)
            }

            val integerString = expNumberInfo.integerPart.toString()
            val fractionString = if (expNumberInfo.fractionPart == 0L) "" else expNumberInfo.fractionString
            return FormattedNumber(integerString, fractionString, exponentString)
        }

        private fun roundToPrecision(numberInfo: NumberInfo, precision: Int = 0): NumberInfo {
            val precisionExp = NumberInfo.MAX_SUPPORTED_FRACTION_VALUE / 10.0.pow(precision)
            var fraction = ((numberInfo.fractionPart.toDouble() / precisionExp).roundToLong() * precisionExp).toLong()
            if (fraction == NumberInfo.MAX_SUPPORTED_FRACTION_VALUE.toLong()) {
                fraction = 0
            }

            val integerPart = (round(numberInfo.number * 10.0.pow(precision)) / 10.0.pow(precision)).toLong()

            val num = integerPart.toDouble() + fraction.toDouble() / NumberInfo.MAX_SUPPORTED_FRACTION_VALUE

            return numberInfo.copy(number = num, fractionPart = fraction, integerPart = integerPart)
        }

        private fun group(str: String) = str
            .reversed() // 1234 -> 4321
            .asSequence() // [4,3,2,1]
            .chunked(GROUP_SIZE) // [[4,3,2], [1]]
            .map { it.joinToString("") } // [[432], [1]]
            .joinToString(COMMA) // 432,1
            .reversed() // 1,234
    }
}


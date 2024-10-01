/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.NumberInfo.Companion.createNumberInfo
import kotlin.math.*


class NumberFormat(spec: Spec) {
    constructor(spec: String) : this(parseSpec(spec))

    private val spec: Spec = normalizeSpec(spec)

    data class Spec(
        val fill: String = " ",
        val align: String = ">",
        val sign: String = "-",
        val symbol: String = "",
        val zero: Boolean = false,
        val width: Int = DEF_WIDTH,
        val comma: Boolean = false,
        val precision: Int = DEF_PRECISION,
        val type: String = "",
        val trim: Boolean = false,
        val expType: ExponentNotationType = DEF_EXPONENT_NOTATION_TYPE,
        val minExp: Int = DEF_MIN_EXP,
        val maxExp: Int = precision
    )

    fun apply(num: Number): String {
        val nonNumberString = handleNonNumbers(num)
        if (nonNumberString != null) {
            return nonNumberString
        }

        val numberInfo = createNumberInfo(num)
        var output = Output()

        output = computeBody(output, numberInfo)
        output = trimFraction(output)

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

    private fun handleNonNumbers(num: Number): String? {
        val number = num.toDouble()
        return when {
            number.isNaN() -> "NaN"
            number == Double.NEGATIVE_INFINITY -> "-Infinity"
            number == Double.POSITIVE_INFINITY -> "+Infinity"
            else -> null
        }
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

        val width = (spec.width - body.fractionalLength - body.exponentialLength)
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
            "%" -> toFixedFormat(createNumberInfo(numberInfo.number * 100), spec.precision)
            "c" -> FormattedNumber(numberInfo.number.toString())
            "d" -> toSimpleFormat(numberInfo, 0)
            "e" -> toSimpleFormat(toExponential(numberInfo, spec.precision), spec.precision)
            "f" -> toFixedFormat(numberInfo, spec.precision)
            "g" -> toPrecisionFormat(numberInfo, spec.precision)
            "b" -> FormattedNumber(numberInfo.number.roundToLong().toString(2))
            "o" -> FormattedNumber(numberInfo.number.roundToLong().toString(8))
            "X" -> FormattedNumber(numberInfo.number.roundToLong().toString(16).uppercase())
            "x" -> FormattedNumber(numberInfo.number.roundToLong().toString(16))
            "s" -> toSiFormat(numberInfo, spec.precision)
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(body = formattedNumber)
    }

    private fun toExponential(numberInfo: NumberInfo, precision: Int = -1): NumberInfo {
        val num = numberInfo.number
        if (num < TYPE_E_MIN) {
            return NumberInfo(0.0)
        }

        var e = if (numberInfo.integerPart == 0L) {
            -(numberInfo.fractionLeadingZeros + 1)
        } else {
            numberInfo.integerLength - 1 +
                    (numberInfo.exponent ?: 0)
        }
        val n = num / 10.0.pow(e)

        var newInfo = createNumberInfo(n)

        if (precision > -1) {
            newInfo = roundToPrecision(newInfo, precision)
        }

        if (newInfo.integerLength > 1) {
            e += 1
            newInfo = createNumberInfo(n / 10)
        }

        return newInfo.copy(exponent = e)
    }

    private fun toPrecisionFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
        if (numberInfo.integerPart == 0L) {
            if (numberInfo.fractionalPart == 0L) {
                return toFixedFormat(numberInfo, precision - 1)
            } else if (numberInfo.fractionLeadingZeros >= -spec.minExp - 1) {
                return toSimpleFormat(toExponential(numberInfo, precision - 1), precision - 1)
            }
            return toFixedFormat(numberInfo, precision + numberInfo.fractionLeadingZeros)
        } else {
            if (numberInfo.integerLength > spec.maxExp) {
                return toSimpleFormat(toExponential(numberInfo, precision - 1), precision - 1)
            }
            return toFixedFormat(numberInfo, precision - numberInfo.integerLength)
        }
    }

    private fun toFixedFormat(numberInfo: NumberInfo, precision: Int = 0): FormattedNumber {
        if (precision <= 0) {
            return FormattedNumber(numberInfo.number.roundToLong().toString())
        }

        val newNumberInfo = roundToPrecision(numberInfo, precision)

        val completePrecision = if (numberInfo.integerLength < newNumberInfo.integerLength) {
            precision - 1
        } else {
            precision
        }

        if (newNumberInfo.fractionalPart == 0L) {
            return FormattedNumber(newNumberInfo.integerPart.toString(), "0".repeat(completePrecision), expType = spec.expType)
        }

        val fractionString = newNumberInfo.fractionString.padEnd(completePrecision, '0')

        return FormattedNumber(newNumberInfo.integerPart.toString(), fractionString)
    }

    private fun toSimpleFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
        val exponentString = buildExponentString(numberInfo.exponent)

        val expNumberInfo =
            createNumberInfo(numberInfo.integerPart + numberInfo.fractionalPart / NumberInfo.MAX_DECIMAL_VALUE.toDouble())

        if (precision > -1) {
            val formattedNumber = toFixedFormat(expNumberInfo, precision)
            return formattedNumber.copy(exponentialPart = exponentString)
        }

        val integerString = expNumberInfo.integerPart.toString()
        val fractionString = if (expNumberInfo.fractionalPart == 0L) "" else expNumberInfo.fractionString
        return FormattedNumber(integerString, fractionString, exponentString, spec.expType)
    }

    private fun buildExponentString(exponent: Int?): String {
        if (exponent == null) {
            return ""
        }
        return if (spec.expType != ExponentNotationType.E) {
            when {
                exponent == 0 && (spec.minExp == null || spec.minExp < 0)
                              && (spec.maxExp == null || spec.maxExp > 0) -> ""
                exponent == 1 && (spec.minExp == null || spec.minExp < 1)
                              && (spec.maxExp == null || spec.maxExp > 1) -> MULT_SIGN + "10"
                else -> MULT_SIGN + "\\(10^{${exponent}}\\)"
            }
        } else {
            val expSign = if (exponent.sign >= 0) "+" else ""
            "e$expSign${exponent}"
        }
    }

    private fun toSiFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
        val expNumberInfo = if (numberInfo.exponent == null) {
            toExponential(numberInfo, precision - 1)
        } else {
            numberInfo
        }
        val exponent = expNumberInfo.exponent ?: 0
        val suffixExp = floor(exponent / 3.0).coerceAtLeast(-8.0).coerceAtMost(8.0).toInt() * 3
        val newNumberInfo = createNumberInfo(numberInfo.number * 10.0.pow(-suffixExp))

        val suffixIndex = 8 + suffixExp / 3
        val exponentString = SI_SUFFIXES[suffixIndex]
        val formattedNumber = toFixedFormat(newNumberInfo, precision - newNumberInfo.integerLength)
        return formattedNumber.copy(exponentialPart = exponentString)
    }

    private fun roundToPrecision(numberInfo: NumberInfo, precision: Int = 0): NumberInfo {
        val exp = numberInfo.exponent ?: 0
        val totalPrecision = precision + exp

        var fractionalPart: Long
        var integerPart: Long

        if (totalPrecision < 0) {
            fractionalPart = 0L
            val intShift = totalPrecision.absoluteValue
            integerPart = if (numberInfo.integerLength <= intShift) {
                0
            } else {
                numberInfo.integerPart / 10.0.pow(intShift).toLong() * 10.0.pow(intShift).toLong()
            }
        } else {
            val precisionExp = NumberInfo.MAX_DECIMAL_VALUE / 10.0.pow(totalPrecision).toLong()
            fractionalPart = if (precisionExp == 0L) {
                numberInfo.fractionalPart
            } else {
                (numberInfo.fractionalPart.toDouble() / precisionExp).roundToLong() * precisionExp
            }
            integerPart = numberInfo.integerPart
            if (fractionalPart == NumberInfo.MAX_DECIMAL_VALUE) {
                fractionalPart = 0
                ++integerPart
            }
        }

        val num = integerPart + fractionalPart.toDouble() / NumberInfo.MAX_DECIMAL_VALUE

        return numberInfo.copy(number = num, fractionalPart = fractionalPart, integerPart = integerPart)
    }

    private fun trimFraction(output: Output): Output {
        if (!spec.trim || output.body.fractionalPart.isEmpty()) {
            return output
        }

        val trimmedFraction = output.body.fractionalPart.trimEnd('0')
        return output.copy(
            body = output.body.copy(
                fractionalPart = trimmedFraction
            )
        )
    }

    private fun computeSign(output: Output, numberInfo: NumberInfo): Output {
        val isBodyZero = output.body.run { (integerPart.asSequence() + fractionalPart.asSequence()).all { it == '0' } }

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
            "#" -> if ("boxX".indexOf(spec.type) > -1) "0${spec.type.lowercase()}" else ""
            else -> ""
        }
        return output.copy(prefix = prefix)
    }

    private fun computeSuffix(res: Output): Output {
        return res.copy(
            suffix = PERCENT.takeIf { spec.type == "%" }.orEmpty()
        )
    }

    private fun computePadding(output: Output): Output {
        val length = output.sign.length + output.prefix.length + output.body.fullLength + output.suffix.length
        val padding = if (length < spec.width) spec.fill.repeat(spec.width - length) else ""
        return output.copy(padding = padding)
    }

    private data class Output(
        val body: FormattedNumber = FormattedNumber(),
        val sign: String = "",
        val prefix: String = "",
        val suffix: String = "",
        val padding: String = ""
    )

    private data class FormattedNumber(
        val integerPart: String = "",
        val fractionalPart: String = "",
        val exponentialPart: String = "",
        val expType: ExponentNotationType = ExponentNotationType.E
    ) {
        val integerLength = if (omitUnit()) 0 else integerPart.length
        val fractionalLength = if (fractionalPart.isEmpty()) 0 else fractionalPart.length + FRACTION_DELIMITER.length
        val exponentialLength: Int
            get() {
                val match = POWER_REGEX.find(exponentialPart) ?: return exponentialPart.length
                val matchGroups = match.groups as MatchNamedGroupCollection
                return matchGroups["degree"]?.value?.length?.plus(2)?.let { d -> if (omitUnit()) d - 1 else d }
                    ?: exponentialPart.length
            }
        val fullLength = integerLength + fractionalLength + exponentialLength

        override fun toString(): String {
            val fractionDelimiter = FRACTION_DELIMITER.takeIf { fractionalPart.isNotEmpty() } ?: ""
            val fullString = "$integerPart$fractionDelimiter$fractionalPart$exponentialPart"
            return if (omitUnit()) {
                fullString.replace("1$MULT_SIGN", "")
            } else {
                fullString
            }
        }

        // Number of the form 1·10^n should be transformed to 10^n if expType is POW
        private fun omitUnit(): Boolean = expType == ExponentNotationType.POW && integerPart == "1" && fractionalPart.isEmpty() && exponentialPart.isNotEmpty()

        companion object {
            @Suppress("RegExpRedundantEscape") // breaks tests
            private val POWER_REGEX = """^${MULT_SIGN}\\\(10\^\{(?<degree>-?\d+)\}\\\)$""".toRegex()
        }
    }

    internal data class NumberInfo(
        val number: Double = 0.0,
        val negative: Boolean = false,
        val integerPart: Long = 0,
        val fractionalPart: Long = 0,
        val exponent: Int? = null
    ) {
        constructor(
            number: Number,
            integerPart: Long = 0,
            fractionalPart: Long = 0,
            exponent: Int? = null
        ) : this(number.toDouble().absoluteValue, number.toDouble() < 0.0, integerPart, fractionalPart, exponent)

        val fractionLeadingZeros = MAX_DECIMALS - length(fractionalPart)
        val integerLength = length(integerPart)
        val fractionString = "0".repeat(fractionLeadingZeros) + fractionalPart.toString().trimEnd('0')

        companion object {
            /**
             * max fraction length we can format (as any other format library does)
             */
            private const val MAX_DECIMALS = 18
            internal val MAX_DECIMAL_VALUE = 10.0.pow(MAX_DECIMALS).toLong()

            internal fun createNumberInfo(num: Number): NumberInfo {
                // frac: "123", exp: 8, double: 0.00000123
                //   -> long: 000_001_230_000_000_000 (extended to max decimal digits)
                val encodeFraction = { frac: String, exp: Int ->
                    var fraction = frac
                    // cutting the fraction if it longer than max decimal digits
                    if (exp > MAX_DECIMALS) {
                        fraction = frac.substring(0 until (frac.length - (exp - MAX_DECIMALS)))
                    }
                    fraction.toLong() * 10.0.pow((MAX_DECIMALS - exp).coerceAtLeast(0)).toLong()
                }

                val (intStr, fracStr, exponentString) =
                    "^(\\d+)\\.?(\\d+)?e?([+-]?\\d+)?\$"
                        .toRegex()
                        .find(num.toDouble().absoluteValue.toString().lowercase())
                        ?.destructured
                        ?: error("Wrong number: $num")

                val exponent: Int = exponentString.toIntOrNull() ?: 0

                // number = 1.23456E+55
                if (exponent.absoluteValue >= MAX_DECIMALS) {
                    return NumberInfo(
                        number = num,
                        // "1" -> 1
                        integerPart = intStr.toLong(),
                        // fraction part ignored intentionally
                        fractionalPart = 0,
                        // 55
                        exponent = exponent
                    )
                }

                check(exponent < MAX_DECIMALS)
                // number = 1.23E-4. double: 0.000123
                if (exponent < 0) {
                    return NumberInfo(
                        number = num,
                        // "1" + "23" -> 000_123_000_000_000_000L
                        fractionalPart = encodeFraction(intStr + fracStr, exponent.absoluteValue + fracStr.length)
                    )
                }

                check(exponent in 0..MAX_DECIMALS)
                // number = 1.234E+5, double: 123400.0
                if (exponent >= fracStr.length) {
                    return NumberInfo(
                        number = num,
                        // "1" + "234" + "00" -> 123400
                        integerPart = (intStr + fracStr + "0".repeat(exponent - fracStr.length)).toLong()
                    )
                }

                check(exponent >= 0 && exponent < fracStr.length)
                // number = 1.234567E+3, double: 1234.567
                return NumberInfo(
                    number = num,
                    // "1" + "[234]567" -> 1234
                    integerPart = (intStr + fracStr.substring(0 until exponent)).toLong(),
                    // "234[567]" -> 567_000_000_000_000_000
                    fractionalPart = fracStr.substring(exponent).run { encodeFraction(this, this.length) }
                )
            }

            private fun length(v: Long): Int {
                // log10 doesn't work for values 10^17 + 1, returning 17.0 instead of 17.00001

                if (v == 0L) {
                    return 1
                }

                var len = 0
                var rem = v
                while (rem > 0) {
                    len++
                    rem /= 10
                }

                return len
            }
        }
    }

    enum class ExponentNotationType(val symbol: String) {
        E("E"),
        POW("P"),
        POW_FULL("F");

        companion object {
            val REGEXP = "[${entries.joinToString("") { it.symbol }}]"

            fun bySymbol(symbol: String): ExponentNotationType {
                return entries.first { it.symbol == symbol }
            }
        }
    }

    companion object {
        fun isValidPattern(spec: String) = NUMBER_REGEX.matches(spec)

        fun parseSpec(spec: String): Spec {
            val matchResult =
                NUMBER_REGEX.find(spec) ?: throw IllegalArgumentException("Wrong number format pattern: '$spec'")
            val precision = matchResult.groups["precision"]?.value?.toInt() ?: DEF_PRECISION
            val formatSpec = Spec(
                fill = matchResult.groups["fill"]?.value ?: " ",
                align = matchResult.groups["align"]?.value ?: ">",
                sign = matchResult.groups["sign"]?.value ?: "-",
                symbol = matchResult.groups["symbol"]?.value ?: "",
                zero = matchResult.groups["zero"] != null,
                width = matchResult.groups["width"]?.value?.toInt() ?: DEF_WIDTH,
                comma = matchResult.groups["comma"] != null,
                precision = precision,
                trim = matchResult.groups["trim"] != null,
                type = matchResult.groups["type"]?.value ?: "",
                expType = matchResult.groups["exptype"]?.value?.let { ExponentNotationType.bySymbol(it) } ?: DEF_EXPONENT_NOTATION_TYPE,
                minExp = matchResult.groups["minexp"]?.value?.toInt() ?: DEF_MIN_EXP,
                maxExp = matchResult.groups["maxexp"]?.value?.toInt() ?: precision,
            )

            return normalizeSpec(formatSpec)
        }

        const val DEF_MIN_EXP = -7 // Number that triggers exponential notation (too small value to be formatted as a simple number). Same as in JS (see toPrecision) and D3.format.

        internal const val TYPE_E_MIN = 1E-323 // Will likely crash on smaller numbers.
        internal const val TYPE_S_MAX = 1E26  // The largest supported SI-prefix is Y - yotta (1.E24).

        private const val CURRENCY = "$"
        private const val PERCENT = "%"
        private const val COMMA = ","
        private const val FRACTION_DELIMITER = "."
        private const val MULT_SIGN = "·"
        private const val GROUP_SIZE = 3
        private val SI_SUFFIXES =
            arrayOf("y", "z", "a", "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")
        private val NUMBER_REGEX =
            """^(?:(?<fill>[^{}])?(?<align>[<>=^]))?(?<sign>[+ -])?(?<symbol>[#$])?(?<zero>0)?(?<width>\d+)?(?<comma>,)?(?:\.(?<precision>\d+))?(?<trim>~)?(?<type>[%bcdefgosXx])?(?:&(?<exptype>${ExponentNotationType.REGEXP}))?(?:\{(?<minexp>-?\d+)?,(?<maxexp>-?\d+)?\})?$""".toRegex()
        private const val DEF_WIDTH = -1
        private const val DEF_PRECISION = 6
        private val DEF_EXPONENT_NOTATION_TYPE = ExponentNotationType.E

        internal fun normalizeSpec(spec: Spec): Spec {
            var precision = spec.precision
            var type = spec.type
            var trim = spec.trim
            if (type == "") {
                if (precision == -1) {
                    precision = 12
                }
                type = "g"
            }

            if (type == "g") {
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

        private fun group(str: String) = str
            .reversed() // 1234 -> 4321
            .asSequence() // [4,3,2,1]
            .chunked(GROUP_SIZE) // [[4,3,2], [1]]
            .map { it.joinToString("") } // [[432], [1]]
            .joinToString(COMMA) // 432,1
            .reversed() // 1,234
    }
}

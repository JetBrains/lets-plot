/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberInfo.Companion.createNumberInfo
import kotlin.math.ceil
import kotlin.math.roundToLong
import kotlin.math.sign


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
            "c" -> FormattedNumber(numberInfo.integerString)
            "d" -> toSimpleFormat(numberInfo, e = false, precision = 0)
            "e" -> toSimpleFormat(numberInfo, e = true, spec.precision)
            "f" -> toFixedFormat(numberInfo, spec.precision)
            "g" -> gFormat(numberInfo, spec.precision)
            "b" -> FormattedNumber(numberInfo.number.roundToLong().toString(2))
            "o" -> FormattedNumber(numberInfo.number.roundToLong().toString(8))
            "X" -> FormattedNumber(numberInfo.number.roundToLong().toString(16).uppercase())
            "x" -> FormattedNumber(numberInfo.number.roundToLong().toString(16))
            "s" -> sFormat(numberInfo, spec.precision)
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(body = formattedNumber)
    }

    private fun gFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
        if (numberInfo.isIntegerZero) {
            if (numberInfo.isFractionZero) {
                return toFixedFormat(numberInfo, precision - 1)
            } else if (numberInfo.fractionLeadingZeros >= -spec.minExp - 1) {
                return toSimpleFormat(numberInfo, e = true, precision - 1)
            }
            return toFixedFormat(numberInfo, precision + numberInfo.fractionLeadingZeros)
        } else {
            if (numberInfo.integerLength > spec.maxExp) {
                return toSimpleFormat(numberInfo, e = true, precision - 1)
            }
            return toFixedFormat(numberInfo, precision - numberInfo.integerLength)
        }
    }

    private fun toFixedFormat(numberInfo: NumberInfo, precision: Int = 0): FormattedNumber {
        if (precision <= 0) {
            return FormattedNumber(numberInfo.integerString)
        }

        val newNumberInfo = numberInfo.fRound(precision)

        val completePrecision = if (numberInfo.integerLength < newNumberInfo.integerLength) {
            precision - 1
        } else {
            precision
        }

        if (newNumberInfo.isFractionZero) {
            return FormattedNumber(newNumberInfo.integerString, "0".repeat(completePrecision), expType = spec.expType)
        }

        val fractionString = newNumberInfo.fractionString.padEnd(completePrecision, '0')

        return FormattedNumber(newNumberInfo.integerString, fractionString)
    }

    private fun toSimpleFormat(numberInfo: NumberInfo, e: Boolean, precision: Int = -1): FormattedNumber {
        // TODO: move zero check to the NumericBreakFormatter
        val exponentString = if (e && !numberInfo.isZero) buildExponentString(numberInfo) else ""

        if (e) {
            val normalized = numberInfo.normalize()

            if (precision > -1) {
                val formattedNumber = toFixedFormat(normalized, precision)
                return formattedNumber.copy(exponentialPart = exponentString, expType = spec.expType)
            }

            val fractionString = if (normalized.decimal.isFractionalPartZero) "" else normalized.fractionString
            return FormattedNumber(normalized.integerString, fractionString, exponentString, spec.expType)
        } else {

            val expNumberInfo =
                createNumberInfo(numberInfo.integerPart + numberInfo.fractionalPart / NumberInfo.MAX_DECIMAL_VALUE.toDouble())

            if (precision > -1) {
                val formattedNumber = toFixedFormat(expNumberInfo, precision)
                return formattedNumber.copy(exponentialPart = exponentString, expType = spec.expType)
            }

            val integerString = expNumberInfo.integerPart.toString()
            val fractionString = if (expNumberInfo.decimal.isFractionalPartZero) "" else expNumberInfo.fractionString
            return FormattedNumber(integerString, fractionString, exponentString, spec.expType)
        }
    }

    private fun buildExponentString(numberInfo: NumberInfo): String {
        val exponent = numberInfo.decimal.toFloating().e

        return if (spec.expType != ExponentNotationType.E) {
            when {
                exponent == 0 && spec.minExp < 0 && spec.maxExp > 0 -> ""
                exponent == 1 && spec.minExp < 1 && spec.maxExp > 1 -> MULT_SIGN + "10"
                else -> MULT_SIGN + "\\(10^{${exponent}}\\)"
            }
        } else {
            val expSign = if (exponent.sign >= 0) "+" else ""
            "e$expSign${exponent}"
        }
    }

    private fun sFormat(numberInfo: NumberInfo, precision: Int = -1): FormattedNumber {
        val siPrefix = eToSiPrefix(numberInfo.decimal.toFloating().e)

        // 23_456.789 -> 23.456_789k
        // 0.000_123_456 -> 123.456u
        val siNormalizedNumber = numberInfo.shiftDecimalPoint(-siPrefix.baseExp)
        val roundedNumber = siNormalizedNumber.iRound(precision)

        val (finalNumber, finalSiPrefix) = if (
            // !! is safe - int part of the si normalized number can't be bigger than 1000
            roundedNumber.decimal.intVal!! == 1000L // 999.999 -> 1000 rounding happened
            && hasNextSiPrefix(siPrefix)
        ) {
            // 1000.0k -> 1.0M
            roundedNumber.shiftDecimalPoint(-3) to getNextSiPrefix(siPrefix)
        } else {
            roundedNumber to siPrefix
        }

        val restPrecision = precision - finalNumber.integerLength
        val formattedNumber = toFixedFormat(finalNumber, restPrecision)
        return formattedNumber.copy(exponentialPart = finalSiPrefix.symbol)
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
                val degreeLength = matchGroups["degree"]?.value?.length ?: return exponentialPart.length
                val fullLength = 2 + degreeLength // 2 for "10" in the "10^d"
                return if (omitUnit()) fullLength else 1 + fullLength // 1 for "·" in the "·10^d"
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
        private fun omitUnit(): Boolean =
            expType == ExponentNotationType.POW && integerPart == "1" && fractionalPart.isEmpty() && exponentialPart.isNotEmpty()

        companion object {
            @Suppress("RegExpRedundantEscape") // breaks tests
            private val POWER_REGEX = """^${MULT_SIGN}\\\(10\^\{(?<degree>-?\d+)\}\\\)$""".toRegex()
        }
    }

    enum class ExponentNotationType(val symbol: String) {
        E("E"),
        POW("P"),
        POW_FULL("F");

        companion object {
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
                expType = matchResult.groups["exptype"]?.value?.let { ExponentNotationType.bySymbol(it) }
                    ?: DEF_EXPONENT_NOTATION_TYPE,
                minExp = matchResult.groups["minexp"]?.value?.toInt() ?: DEF_MIN_EXP,
                maxExp = matchResult.groups["maxexp"]?.value?.toInt() ?: precision,
            )

            return normalizeSpec(formatSpec)
        }

        enum class SiPrefix(
            val symbol: String,
            val expRange: IntRange,
        ) {
            YOTTA("Y", 24 until 27),
            ZETTA("Z", 21 until 24),
            EXA("E", 18 until 21),
            PETA("P", 15 until 18),
            TERA("T", 12 until 15),
            GIGA("G", 9 until 12),
            MEGA("M", 6 until 9),
            KILO("k", 3 until 6),
            NONE("", 0 until 3),
            MILLI("m", -3 until 0),
            MICRO("µ", -6 until -3),
            NANO("n", -9 until -6),
            PICO("p", -12 until -9),
            FEMTO("f", -15 until -12),
            ATTO("a", -18 until -15),
            ZEPTO("z", -21 until -18),
            YOCTO("y", -24 until -21);

            val baseExp = expRange.first
        }

        const val DEF_MIN_EXP =
            -7 // Number that triggers exponential notation (too small value to be formatted as a simple number). Same as in JS (see toPrecision) and D3.format.

        internal const val TYPE_E_MIN = 1E-323 // Will likely crash on smaller numbers.
        internal const val TYPE_S_MAX = 1E26  // The largest supported SI-prefix is Y - yotta (1.E24).

        private const val CURRENCY = "$"
        private const val PERCENT = "%"
        private const val COMMA = ","
        private const val FRACTION_DELIMITER = "."
        private const val MULT_SIGN = "·"
        private const val GROUP_SIZE = 3
        private val EXPONENT_TYPES_REGEX = "[${ExponentNotationType.entries.joinToString("") { it.symbol }}]"
        private val NUMBER_REGEX =
            """^(?:(?<fill>[^{}])?(?<align>[<>=^]))?(?<sign>[+ -])?(?<symbol>[#$])?(?<zero>0)?(?<width>\d+)?(?<comma>,)?(?:\.(?<precision>\d+))?(?<trim>~)?(?<type>[%bcdefgosXx])?(?:&(?<exptype>$EXPONENT_TYPES_REGEX))?(?:\{(?<minexp>-?\d+)?,(?<maxexp>-?\d+)?\})?$""".toRegex()
        private const val DEF_WIDTH = -1
        private const val DEF_PRECISION = 6
        private val DEF_EXPONENT_NOTATION_TYPE = ExponentNotationType.E

        internal fun eToSiPrefix(e: Int): SiPrefix {
            val prefix = SiPrefix.entries.firstOrNull() { e in it.expRange }
            if (prefix != null) return prefix

            return if (e < 0) {
                SiPrefix.entries.minBy { it.expRange.first }
            } else {
                SiPrefix.entries.maxBy { it.expRange.last }
            }
        }

        internal fun hasNextSiPrefix(prefix: SiPrefix): Boolean {
            return prefix.ordinal > 0
        }

        internal fun getNextSiPrefix(prefix: SiPrefix): SiPrefix {
            return if (prefix.ordinal > 0) {
                SiPrefix.entries[prefix.ordinal - 1]
            } else {
                prefix
            }
        }

        internal fun normalizeSpec(spec: Spec): Spec {
            var precision = spec.precision
            var type = spec.type
            var trim = spec.trim
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

        private fun group(str: String) = str
            .reversed() // 1234 -> 4321
            .asSequence() // [4,3,2,1]
            .chunked(GROUP_SIZE) // [[4,3,2], [1]]
            .map { it.joinToString("") } // [[432], [1]]
            .joinToString(COMMA) // 432,1
            .reversed() // 1,234
    }
}

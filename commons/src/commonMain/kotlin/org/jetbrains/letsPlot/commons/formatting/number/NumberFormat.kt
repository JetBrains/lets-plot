/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.formatting.number

import org.jetbrains.letsPlot.commons.formatting.number.NumberFormat.ExponentNotationType.entries
import kotlin.math.absoluteValue
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
    ) {
        fun toStringPattern(): String {
            val commaStr = COMMA.takeIf { comma } ?: ""
            val zeroStr = "0".takeIf { zero } ?: ""
            val widthStr = width.takeIf { width != DEF_WIDTH }?.toString() ?: ""
            val trimStr = "~".takeIf { trim } ?: ""
            val expTypeStr = expType.symbol
            val minExpStr = minExp.takeIf { minExp != DEF_MIN_EXP }?.toString() ?: ""
            val maxExpStr = maxExp.takeIf { maxExp != precision }?.toString() ?: ""
            return "$fill$align$sign$symbol$zeroStr$widthStr$commaStr.$precision$trimStr$type&$expTypeStr{$minExpStr,$maxExpStr}"
        }
    }

    fun apply(num: Number): String {
        val nonNumberString = handleNonNumbers(num)
        if (nonNumberString != null) {
            return nonNumberString
        }

        val number = Decimal.fromNumber(num)
        var output = Output()

        output = computeBody(output, number)
        output = trimFraction(output)

        output = computeSign(output, number)
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

    private fun computeBody(res: Output, number: Decimal): Output {
        val formattedNumber = when (spec.type) {
            "e" -> formatExponentNotation(number, spec.precision) // scientific notation, e.g. 12345 -> "1.234500e+4"
            "f" -> formatDecimalNotation(number, spec.precision) // fixed-point notation, e.g. 1.5(6) -> "1.500000", 1.5(0) -> "2"
            "d" -> FormattedNumber(number.fRound(0).wholePart) // rounded to integer, e.g. 1.5 -> "2"
            "%" -> formatDecimalNotation(number.shiftDecimalPoint(2), spec.precision) // percentage, e.g. 0.015 -> "1.500000%"
            "g" -> generalFormat(number, spec.precision) // general format, e.g. 1e3 -> "1000.00, 1e10 -> "1.00000e+10", 1e-3 -> "0.00100000", 1e-10 -> "1.00000e-10"
            "s" -> siPrefixFormat(number, spec.precision) // SI-prefix notation, e.g. 1e3 -> "1.00000k"
            "c" -> FormattedNumber(number.wholePart)
            "b" -> FormattedNumber(number.toDouble().absoluteValue.roundToLong().toString(2))
            "o" -> FormattedNumber(number.toDouble().absoluteValue.roundToLong().toString(8))
            "X" -> FormattedNumber(number.toDouble().absoluteValue.roundToLong().toString(16).uppercase())
            "x" -> FormattedNumber(number.toDouble().absoluteValue.roundToLong().toString(16))
            else -> throw IllegalArgumentException("Wrong type: ${spec.type}")
        }
        return res.copy(body = formattedNumber)
    }

    private fun generalFormat(number: Decimal, precision: Int): FormattedNumber {
        // Can't be both zero - rounding in decimal notation will give incorrect results
        // Yet it's ok to have precision > 0 and spec.maxExp == 0 to trigger exponential notation for all numbers,
        // including integers (e.g. 1 -> 1e+0 to trigger power notation)
        val (significantDigitsCount, maxExp) = when {
            precision == 0 && spec.maxExp == 0 -> 1 to 1
            else -> precision to spec.maxExp
        }

        if (number.isZero) {
            return formatDecimalNotation(Decimal.ZERO, significantDigitsCount - 1)
        }

        if (number.isWholePartZero && number.asFloat.exp > spec.minExp) {
            // -1 for the zero in the whole part
            return formatDecimalNotation(number, significantDigitsCount - 1 - number.asFloat.exp)
        }

        if (!number.isWholePartZero && number.wholePart.length <= maxExp) {
            return formatDecimalNotation(number, significantDigitsCount - number.wholePart.length)
        }

        return formatExponentNotation(number, (significantDigitsCount - 1).coerceAtLeast(0))
    }

    // (9.925, 0) -> "10"
    // (123.925, 0) -> "124"
    // (1.925, 6) -> "1.925000"
    // (1.925, 2) -> "1.93"
    // (12345678, 2) -> "12345678.00"
    private fun formatDecimalNotation(number: Decimal, precision: Int): FormattedNumber {
        if (precision <= 0) {
            return FormattedNumber(number.fRound(0).wholePart)
        }

        val floating = number.asFloat
        val fRounded = floating.toDecimalPrecision(precision)

        val (w, d) = fRounded.toDecimalStr(precision)

        return FormattedNumber(w, d)
    }

    private fun formatExponentNotation(number: Decimal, precision: Int): FormattedNumber {
        if (precision > -1) {
            val rounded = number.asFloat.toPrecision(precision)
            return FormattedNumber(
                integerPart = rounded.significand.toString(),
                fractionalPart = rounded.fraction.take(precision).padEnd(precision, '0'),
                exponentialPart = buildExponentString(rounded.exp),
                expType = spec.expType
            )
        } else {
            // Format without ".0" fractional part when number with one significant digit (e.g., 1.0, 0.1, 0.0004)
            // 0.0 -> "0"
            // 1.0 -> "1e+0"
            // 0.1 -> "1e-1"
            if (number.isZero) {
                return FormattedNumber("0", "", "")
            }

            val floating = number.asFloat
            return FormattedNumber(
                integerPart = floating.significand.toString(),
                fractionalPart = floating.fraction.takeIf { it != "0" } ?: "", // 1.0e0 -> 1e0
                exponentialPart = buildExponentString(floating.exp),
                expType = spec.expType
            )
        }
    }

    private fun buildExponentString(exp: Int): String = when (spec.expType) {
        ExponentNotationType.E -> "e${if (exp.sign >= 0) "+" else ""}$exp"
        ExponentNotationType.POW, ExponentNotationType.POW_FULL -> when {
            exp == 0 && spec.minExp < 0 && spec.maxExp > 0 -> ""
            exp == 1 && spec.minExp < 1 && spec.maxExp > 1 -> "${MULT_SIGN}10"
            else -> "$MULT_SIGN\\(10^{$exp}\\)"
        }
    }

    private fun siPrefixFormat(number: Decimal, precision: Int = -1): FormattedNumber {
        val siPrefix = siPrefixFromExp(number.asFloat.exp)

        // 23_456.789 -> 23.456_789k
        // 0.000_123_456 -> 123.456u
        val siNormalizedNumber = number.shiftDecimalPoint(-siPrefix.baseExp)
        val roundedNumber = siNormalizedNumber.iRound(precision)

        val (finalNumber, finalSiPrefix) = if (
        // !! is safe - int part of the si normalized number can't be bigger than 1000
            roundedNumber.wholeValue!! == 1000L // 999.999 -> 1000 rounding happened
            && hasNextSiPrefix(siPrefix)
        ) {
            // 1000.0k -> 1.0M
            roundedNumber.shiftDecimalPoint(-3) to getNextSiPrefix(siPrefix)
        } else {
            roundedNumber to siPrefix
        }

        val restPrecision = precision - finalNumber.wholePart.length
        val formattedNumber = formatDecimalNotation(finalNumber, restPrecision)
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

    private fun computeSign(output: Output, number: Decimal): Output {
        val isBodyZero = output.body.run { (integerPart.asSequence() + fractionalPart.asSequence()).all { it == '0' } }

        val isNegative = number.isNegative && !isBodyZero
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

        const val DEF_MIN_EXP =
            -7 // Number that triggers exponential notation (too small value to be formatted as a simple number). Same as in JS (see toPrecision) and D3.format.

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

        internal fun siPrefixFromExp(exp: Int): SiPrefix {
            val prefix = SiPrefix.entries.firstOrNull { exp in it.expRange }
            if (prefix != null) return prefix

            return if (exp < 0) {
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

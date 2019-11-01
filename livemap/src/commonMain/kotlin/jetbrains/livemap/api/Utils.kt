package jetbrains.livemap.api

import kotlin.math.PI
import kotlin.math.abs

private const val ONE_HUNDRED_PERCENTS = 1.0
private const val MIN_PERCENT = 0.05

fun transformValues2Percents(values: List<Double>, maxAbsValue: Double): List<Double> {
    return values.map { calculatePercent(it, maxAbsValue) }
}

fun transformValues2Angles(values: List<Double>): List<Double> {
    val sum = values.map { abs(it) }.sum()

    return if (sum == 0.0) {
        MutableList(values.size) { 2 * PI / values.size }
    } else {
        values.map { 2 * PI * abs(it) / sum }
    }
}

private fun calculatePercent(value: Double, maxAbsValue: Double): Double {
    val percent = if (maxAbsValue == 0.0) 0.0 else ONE_HUNDRED_PERCENTS * value / maxAbsValue

    if (abs(percent) >= MIN_PERCENT) {
        return percent
    }
    return if (percent >= 0) MIN_PERCENT else -MIN_PERCENT
}
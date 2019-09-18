package jetbrains.livemap.mapobjects

import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.livemap.api.BarSource
import jetbrains.livemap.projections.Client
import kotlin.math.abs

object Utils {
    private const val ONE_HUNDRED_PERCENTS = 1.0
    private const val MIN_PERCENT = 0.05

    fun splitMapBarChart(source: BarSource, maxAbsValue: Double): List<MapBar> {
        val result = ArrayList<MapBar>()
        val percents = transformValues2Percents(source.values, maxAbsValue)

        val radius = source.radius
        val barCount = percents.size
        val spacing = 0.1 * radius
        val barWidth = (2 * radius - (barCount - 1) * spacing) / barCount

        for (i in percents.indices) {
            val barDimension =  explicitVec<Client>(barWidth, radius * abs(percents[i]))
            val barOffset = explicitVec<Client>(
                (barWidth + spacing) * i - radius,
                if (percents[i] > 0) -barDimension.y else 0.0
            )
            result.add(
                MapBar(
                    source.indices[i],
                    "",
                    "",
                    explicitVec(source.lon, source.lat),
                    source.colors[i],
                    source.strokeColor,
                    source.strokeWidth,
                    barDimension,
                    barOffset
                )
            )
        }
        return result
    }

    private fun transformValues2Percents(values: List<Double>, maxAbsValue: Double): List<Double> {
        return values.map { calculatePercent(it, maxAbsValue) }
    }

    private fun calculatePercent(value: Double, maxAbsValue: Double): Double {
        val percent = if (maxAbsValue == 0.0) 0.0 else ONE_HUNDRED_PERCENTS * value / maxAbsValue

        if (abs(percent) >= MIN_PERCENT) {
            return percent
        }
        return if (percent >= 0) MIN_PERCENT else -MIN_PERCENT
    }
}
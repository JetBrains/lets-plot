/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.intern.math.ipow
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.base.stat.*
import kotlin.concurrent.Volatile
import kotlin.math.log2
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime

// These are heuristic performance checks. They are tuned to reduce noise, but an occasional failure can still be spurious.
// Rerun once before treating a failure as a regression.
class StatPerformanceTest {
    @Test
    fun countPerformanceScalesAsExpected() {
        checkPerformance(
            Stats.count(),
            categoricalXDataFrameGenerator(distinctXCount = 256),
            minSize = 8_000
        )
    }

    @Test
    fun count2dPerformanceScalesAsExpected() {
        checkGroupedXyPerformance(Stats.count2d())
    }

    @Test
    fun sumPerformanceScalesAsExpected() {
        checkGroupedXyPerformance(Stats.sum())
    }

    @Test
    fun binDefaultPerformanceScalesAsExpected() {
        checkPerformance(Stats.bin(BinStat.DEF_BIN_COUNT), xDataFrameGenerator())
    }

    @Test
    fun binBreaksPerformanceScalesAsExpected() {
        checkPerformance(
            Stats.bin(breaks = listOf(-6.0, -1.0, 0.0, 1.0, 6.0)),
            uniformRangeXDataFrameGenerator(-6.0, 6.0, distinctXCount = 512),
            minSize = 8_000
        )
    }

    @Test
    fun bin2dDefaultPerformanceScalesAsExpected() {
        checkPerformance(
            Bin2dStat(),
            regularGridXyDataFrameGenerator(gridWidth = 64),
            minSize = 8_192
        )
    }

    @Test
    fun binHexDefaultPerformanceScalesAsExpected() {
        checkPerformance(BinHexStat(), xyDataFrameGenerator())
    }

    @Test
    fun boxplotFiveCategoriesPerformanceScalesAsExpected() {
        checkPerformance(Stats.boxplot(), catyDataFrameGenerator(categoriesCount = 5))
    }

    @Test
    fun boxplotOutlierFiveCategoriesPerformanceScalesAsExpected() {
        checkPerformance(Stats.boxplotOutlier(), catyDataFrameGenerator(categoriesCount = 5))
    }

    @Test
    fun densityDefaultPerformanceScalesAsExpected() {
        checkPerformance(
            Stats.density(),
            xDataFrameGenerator(),
            maxNormalizedDrift = 3.0
        )
    }

    @Test
    fun densityFullScanPerformanceScalesAsExpected() {
        checkPerformance(
            Stats.density(fullScanMax = 1),
            xDataFrameGenerator(),
            maxNormalizedDrift = 3.0
        )
    }

    @Test
    fun density2dContourPerformanceScalesAsExpected() {
        checkDensity2dPerformance(Stats.density2d(
            nX = DENSITY_2D_GRID_SIZE,
            nY = DENSITY_2D_GRID_SIZE,
            isContour = true
        ))
    }

    /*
     * With minSize = 4_000 and nX/nY = 100, it makes the tests in StatPerformanceTest almost three times as slow.
     * With lower values, it is unstable - it fails a few times per 100 runs.
     * For now, we can do without this test.
     */
    /*
    @Test
    fun density2dRasterPerformanceScalesAsExpected() {
        checkDensity2dPerformance(Stats.density2d(nX = 100, nY = 100, isContour = false), minSize = 4_000)
    }
    */

    @Test
    fun density2dfContourPerformanceScalesAsExpected() {
        checkDensity2dPerformance(density2dfStat())
    }

    @Test
    fun density2dfRasterPerformanceScalesAsExpected() {
        checkDensity2dPerformance(density2dfStat(isContour = false))
    }

    @Test
    fun dotplotDefaultPerformanceScalesAsExpected() {
        checkPerformance(Stats.dotplot(), xDataFrameGenerator())
    }

    @Test
    fun dotplotHistodotPerformanceScalesAsExpected() {
        checkPerformance(
            Stats.dotplot(method = DotplotStat.Method.HISTODOT),
            categoricalXDataFrameGenerator(distinctXCount = 256),
            minSize = 8_000,
        )
    }

    @Test
    fun yDotplotDefaultPerformanceScalesAsExpected() {
        checkPerformance(Stats.ydotplot(), catyDataFrameGenerator(categoriesCount = 5))
    }

    @Test
    fun yDotplotHistodotPerformanceScalesAsExpected() {
        checkPerformance(
            Stats.ydotplot(method = DotplotStat.Method.HISTODOT),
            categoricalXDistinctYDataFrameGenerator(categoriesCount = 2, distinctYCount = 512),
            minSize = 8_000,
            rounds = 32,
            maxDoublingRatio = 3.6,
        )
    }

    @Test
    fun contourDefaultPerformanceScalesAsExpected() {
        checkPerformance(Stats.contour(), contourDataFrameGenerator(40))
    }

    @Test
    fun contourfDefaultPerformanceScalesAsExpected() {
        checkPerformance(Stats.contourf(), contourDataFrameGenerator(40))
    }

    @Test
    fun ecdfDefaultPerformanceScalesAsExpected() {
        checkPerformance(ECDFStat(null, false), xDataFrameGenerator())
    }

    @Test
    fun ecdfNPerformanceScalesAsExpected() {
        checkPerformance(ECDFStat(100, false), xDataFrameGenerator())
    }

    @Test
    fun qqNormalPerformanceScalesAsExpected() {
        checkPerformance(Stats.qq(), sampleDataFrameGenerator())
    }

    @Test
    fun qqUniformPerformanceScalesAsExpected() {
        checkPerformance(Stats.qq(distribution = QQStat.Distribution.UNIFORM), sampleDataFrameGenerator())
    }

    @Test
    fun qq2PerformanceScalesAsExpected() {
        checkPerformance(Stats.qq2(), xyDataFrameGenerator())
    }

    @Test
    fun qq2LineDefaultPerformanceScalesAsExpected() {
        checkPerformance(Stats.qq2line(), xyDataFrameGenerator())
    }

    @Test
    fun qq2LineWideQuantilesPerformanceScalesAsExpected() {
        checkPerformance(Stats.qq2line(lineQuantiles = 0.1 to 0.9), xyDataFrameGenerator())
    }

    @Test
    fun qqLineNormalPerformanceScalesAsExpected() {
        checkPerformance(Stats.qqline(), sampleDataFrameGenerator())
    }

    @Test
    fun qqLineUniformPerformanceScalesAsExpected() {
        checkPerformance(Stats.qqline(distribution = QQStat.Distribution.UNIFORM), sampleDataFrameGenerator())
    }

    private data class Sample(val size: Int, val medianNanos: Double)

    @Volatile
    // Keep the result observable so the benchmarked call is not optimized away.
    private var performanceBlackhole: DataFrame? = null

    private fun checkPerformance(
        stat: Stat,
        generateDataFrame: (size: Int, seed: Int) -> DataFrame,
        minSize: Int = DEF_MIN_SIZE,
        sizesCount: Int = DEF_SIZES_COUNT,
        rounds: Int = DEF_ROUNDS,
        maxDoublingRatio: Double = DEF_MAX_DOUBLING_RATIO,
        maxNormalizedDrift: Double = DEF_MAX_NORMALIZED_DRIFT
    ) {
        val applyStat: (DataFrame) -> DataFrame = { df ->
            stat.apply(df, SimpleStatContext(df))
        }
        val sizes = (0 until sizesCount).map { i -> 2.ipow(i).toInt() * minSize }
        val dataFrames = sizes.associateWith { n ->
            List(rounds) { round -> generateDataFrame(n, 10_000 * n + round) }
        }

        warmUp(sizes, dataFrames, applyStat, iterations = 20)
        val samples = measureSamples(sizes, dataFrames, applyStat)

        val doublingRatios = samples.zipWithNext { previous, current ->
            current.medianNanos / previous.medianNanos
        }

        // This heuristic is tuned for stats that are expected to scale roughly like O(N*log(N)).
        // It catches obvious superlinear blow-ups, but stats with materially different growth need a different baseline.
        val normalizedTail = samples
            .drop(2)
            .map { it.medianNanos / (it.size * log2(it.size.toDouble())) }

        assertTrue(
            doublingRatios.max() < maxDoublingRatio,
            "Doubling ratio exceeded: max=${doublingRatios.max()} > $maxDoublingRatio, ratios=$doublingRatios, samples=$samples"
        )

        assertTrue(
            normalizedTail.max() / normalizedTail.min() < maxNormalizedDrift,
            "N*log(N) drift exceeded: drift=${normalizedTail.max() / normalizedTail.min()} > $maxNormalizedDrift, normalized=$normalizedTail, samples=$samples"
        )
    }

    private fun checkDensity2dPerformance(stat: Stat, minSize: Int = 1_000) {
        checkPerformance(
            stat,
            xyDataFrameGenerator(),
            minSize = minSize
        )
    }

    private fun checkGroupedXyPerformance(stat: Stat) {
        checkPerformance(
            stat,
            groupedXyDataFrameGenerator(distinctXCount = 64, distinctYCount = 64),
            minSize = 8_000
        )
    }

    private fun warmUp(
        sizes: List<Int>,
        dataFrames: Map<Int, List<DataFrame>>,
        applyStat: (DataFrame) -> DataFrame,
        iterations: Int
    ) {
        repeat(iterations) { round ->
            // Rotate input sizes to avoid always measuring after the same allocation pattern.
            for (size in sizes.shuffled(Random(round))) {
                val df = dataFrames.getValue(size)[round % dataFrames.getValue(size).size]
                performanceBlackhole = applyStat(df)
            }
        }
    }

    private fun measureSamples(
        sizes: List<Int>,
        inputs: Map<Int, List<DataFrame>>,
        applyStat: (DataFrame) -> DataFrame
    ): List<Sample> {
        val timesBySize = sizes.associateWith { mutableListOf<Long>() }

        val rounds = inputs.values.first().size
        repeat(rounds) { round ->
            for (size in sizes.shuffled(Random(1000 + round))) {
                val input = inputs.getValue(size)[round]

                val duration = measureTime {
                    performanceBlackhole = applyStat(input)
                }
                timesBySize.getValue(size).add(duration.inWholeNanoseconds)
            }
        }

        return sizes.map { size ->
            val times = timesBySize.getValue(size).sorted()
            Sample(size, median(times))
        }
    }

    private fun median(times: List<Long>): Double {
        require(times.isNotEmpty()) { "Cannot compute median for an empty sample" }

        val middle = times.size / 2

        return if (times.size % 2 == 0) {
            (times[middle - 1] + times[middle]) / 2.0
        } else {
            times[middle].toDouble()
        }
    }

    companion object {
        // Tune this carefully: a change may look stable in a few runs but still introduce false positives or false negatives later.
        // Raising it is usually safer; lowering it should be validated with repeated runs.
        private const val DEF_MIN_SIZE = 2_000
        private const val DEF_SIZES_COUNT = 5
        private const val DEF_ROUNDS = 24
        private const val DEF_MAX_DOUBLING_RATIO = 3.4
        private const val DEF_MAX_NORMALIZED_DRIFT = 2.7

        private const val DENSITY_2D_GRID_SIZE = 64

        private fun density2dfStat(
            isContour: Boolean = true,
        ): AbstractDensity2dStat {
            return Stats.density2df(
                bandWidthMethod = AbstractDensity2dStat.DEF_BW,
                nX = DENSITY_2D_GRID_SIZE,
                nY = DENSITY_2D_GRID_SIZE,
                isContour = isContour
            )
        }

        private fun xDataFrameGenerator(): (n: Int, seed: Int) -> DataFrame {
            return dataFrameGenerator({ randomDoubleValues, _, _ ->
                mapOf(
                    TransformVar.X to randomDoubleValues(),
                )
            })
        }

        private fun categoricalXDataFrameGenerator(distinctXCount: Int): (n: Int, seed: Int) -> DataFrame {
            return { n, seed ->
                val offset = seed % distinctXCount
                val xValues = List(n) { index -> ((index + offset) % distinctXCount).toDouble() }
                DataFrame.Builder()
                    .putNumeric(TransformVar.X, xValues)
                    .build()
            }
        }

        private fun xyDataFrameGenerator(): (n: Int, seed: Int) -> DataFrame {
            return dataFrameGenerator({ randomDoubleValues, _, _ ->
                mapOf(
                    TransformVar.X to randomDoubleValues(),
                    TransformVar.Y to randomDoubleValues(),
                )
            })
        }

        private fun groupedXyDataFrameGenerator(
            distinctXCount: Int,
            distinctYCount: Int
        ): (n: Int, seed: Int) -> DataFrame {
            return { n, seed ->
                val xOffset = seed % distinctXCount
                val yOffset = (seed / distinctXCount) % distinctYCount
                val xValues = List(n) { index -> ((index + xOffset) % distinctXCount).toDouble() }
                val yValues = List(n) { index ->
                    (((index / distinctXCount) + yOffset) % distinctYCount).toDouble()
                }
                DataFrame.Builder()
                    .putNumeric(TransformVar.X, xValues)
                    .putNumeric(TransformVar.Y, yValues)
                    .build()
            }
        }

        private fun uniformRangeXDataFrameGenerator(
            from: Double,
            to: Double,
            distinctXCount: Int
        ): (n: Int, seed: Int) -> DataFrame {
            require(distinctXCount > 1) { "distinctXCount must be greater than 1: $distinctXCount" }

            return { n, seed ->
                val offset = seed % distinctXCount
                val span = to - from
                val xValues = List(n) { index ->
                    val scaledIndex = (index + offset) % distinctXCount
                    from + span * scaledIndex / (distinctXCount - 1)
                }
                DataFrame.Builder()
                    .putNumeric(TransformVar.X, xValues)
                    .build()
            }
        }

        private fun regularGridXyDataFrameGenerator(gridWidth: Int): (n: Int, seed: Int) -> DataFrame {
            return { n, seed ->
                require(n % gridWidth == 0) {
                    "Grid benchmark size must be divisible by grid width: size=$n, gridWidth=$gridWidth"
                }

                val xShift = (seed % gridWidth).toDouble() / gridWidth
                val xValues = ArrayList<Double>(n)
                val yValues = ArrayList<Double>(n)

                repeat(n) { index ->
                    val column = index % gridWidth
                    val row = index / gridWidth
                    xValues.add(column + xShift)
                    yValues.add(row.toDouble())
                }

                DataFrame.Builder()
                    .putNumeric(TransformVar.X, xValues)
                    .putNumeric(TransformVar.Y, yValues)
                    .build()
            }
        }

        private fun sampleDataFrameGenerator(): (n: Int, seed: Int) -> DataFrame {
            return dataFrameGenerator({ randomDoubleValues, _, _ ->
                mapOf(
                    TransformVar.SAMPLE to randomDoubleValues(),
                )
            })
        }

        private fun catyDataFrameGenerator(categoriesCount: Int): (n: Int, seed: Int) -> DataFrame {
            return dataFrameGenerator({ randomDoubleValues, randomCategoryValues, _ ->
                mapOf(
                    TransformVar.X to randomCategoryValues(),
                    TransformVar.Y to randomDoubleValues(),
                )
            }, categoriesCount = categoriesCount)
        }

        private fun categoricalXDistinctYDataFrameGenerator(
            categoriesCount: Int,
            distinctYCount: Int
        ): (n: Int, seed: Int) -> DataFrame {
            return { n, seed ->
                val xOffset = seed % categoriesCount
                val yOffset = (seed / categoriesCount) % distinctYCount
                val xValues = List(n) { index -> ((index + xOffset) % categoriesCount).toDouble() }
                val yValues = List(n) { index ->
                    (((index / categoriesCount) + yOffset) % distinctYCount).toDouble()
                }
                DataFrame.Builder()
                    .putNumeric(TransformVar.X, xValues)
                    .putNumeric(TransformVar.Y, yValues)
                    .build()
            }
        }

        private fun contourDataFrameGenerator(gridWidth: Int): (n: Int, seed: Int) -> DataFrame {
            return { n, seed ->
                require(n % gridWidth == 0) {
                    "Contour benchmark size must be divisible by grid width: size=$n, gridWidth=$gridWidth"
                }

                val zShift = (seed % gridWidth).toDouble() / gridWidth
                val xValues = ArrayList<Double>(n)
                val yValues = ArrayList<Double>(n)
                val zValues = ArrayList<Double>(n)

                repeat(n) { index ->
                    val column = index % gridWidth
                    val row = index / gridWidth
                    xValues.add(column.toDouble())
                    yValues.add(row.toDouble())
                    zValues.add(row + (column + zShift) / gridWidth)
                }

                DataFrame.Builder()
                    .putNumeric(TransformVar.X, xValues)
                    .putNumeric(TransformVar.Y, yValues)
                    .putNumeric(TransformVar.Z, zValues)
                    .build()
            }
        }

        private fun dataFrameGenerator(
            dataMapGenerator: (randomDoubleValues: () -> List<Double>, randomCategoryValues: () -> List<Double>, n: Int) -> Map<DataFrame.Variable, List<Double?>>,
            categoriesCount: Int = 1
        ): (n: Int, seed: Int) -> DataFrame {
            return { n, seed ->
                val rnd = Random(seed)
                val dataMap = dataMapGenerator(
                    { List(n) { rnd.nextDouble() } },
                    { List(n) { rnd.nextInt(categoriesCount).toDouble() } },
                    n
                )
                val builder = DataFrame.Builder()
                for ((variable, values) in dataMap) {
                    builder.putNumeric(variable, values)
                }
                builder.build()
            }
        }
    }
}

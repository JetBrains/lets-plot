/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.common.data

internal object MeshGen {
    /**
     * Generates `rows` points (columns of points) for each x in xs.
     */
    fun genGrid(
        xs: List<Double>,
        yStep: Double = 10.0,
    ): XYSeries {
        // columns: 1, 1, 1, 1...2, 2, 2, 2...3, 3, 3, 3...
        val columns = xs.map {
            List(RegularMeshDetector.COLUMN_THRESHOLD) { _ -> it }
        }.flatten()

        // rows: 1, 2, 3, 4...1, 2, 3, 4...1, 2, 3, 4...
        val rows = xs.map {
            List(RegularMeshDetector.ROW_THRESHOLD) { i -> i * yStep }
        }.flatten()

        return XYSeries(columns, rows)
    }

    internal class XYSeries(
        val columns: List<Double>,
        val rows: List<Double>,
    )
}
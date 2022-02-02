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
        rows: Int = RegularMeshDetector.GRID_THRESHOLD,
        yStep: Double = 10.0,
    ): XYSeries {
        val xSerie = xs.map {
            List(rows) { i -> it }
        }.flatten()

        val ySerie = xs.map {
            List(rows) { i -> i * yStep }
        }.flatten()

        return XYSeries(xSerie, ySerie)
    }

    internal class XYSeries(
        val x: List<Double>,
        val y: List<Double>,
    )
}
/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.stat

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataFrame

internal class Contour private constructor() {

    private val myContourX = ArrayList<Double>()
    private val myContourY = ArrayList<Double>()
    private val myContourLevel = ArrayList<Double>()
    private val myContourGroup = ArrayList<Double>()

    private var myGroup = 0.0

    private val dataFrame: DataFrame
        get() = DataFrame.Builder()
                .putNumeric(Stats.X, myContourX)
                .putNumeric(Stats.Y, myContourY)
                .putNumeric(Stats.LEVEL, myContourLevel)
                .putNumeric(Stats.GROUP, myContourGroup)
                .build()

    fun add(polygon: List<DoubleVector>, fillLevel: Double) {
        for (p in polygon) {
            myContourX.add(p.x)
            myContourY.add(p.y)
            myContourLevel.add(fillLevel)
            myContourGroup.add(myGroup)
        }
        // each polygon in its own group
        myGroup += 1.0
    }

    companion object {

        fun getPathDataFrame(
                levels: List<Double>, pathListByLevel: Map<Double, List<List<DoubleVector>>>): DataFrame {
            val contour = Contour()
            for (level in levels) {
                val paths = pathListByLevel[level]!!
                for (path in paths) {
                    contour.add(path, level)
                }
            }
            return contour.dataFrame
        }

        fun getPolygonDataFrame(
                fillLevels: List<Double>, polygonListByFillLevel: Map<Double, List<DoubleVector>>): DataFrame {
            val contour = Contour()

            for (fillLevel in fillLevels) {
                val polygon = polygonListByFillLevel[fillLevel]!!
                contour.add(polygon, fillLevel)
            }

            return contour.dataFrame
        }
    }

}

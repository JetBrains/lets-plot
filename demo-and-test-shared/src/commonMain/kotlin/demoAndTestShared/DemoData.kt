/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object DemoData {
    fun contourDemoData(): Map<String, List<*>> {
        val countX = 20
        val countY = 20

        val mean = DoubleVector(5.0, 5.0)
        val height = 1.0
        val radius = 10.0
        val slop = height / radius
        val x = ArrayList<Double>()
        val y = ArrayList<Double>()
        val z = ArrayList<Double>()
        for (row in 0 until countY) {
            for (col in 0 until countX) {
                val dist = DoubleVector(col.toDouble(), row.toDouble()).subtract(mean).length()
                val v = if (dist >= radius)
                    0.0
                else
                    height - dist * slop

                x.add(col.toDouble())
                y.add(row.toDouble())
                z.add(v)
            }
        }

        val map = HashMap<String, List<*>>()
        map["x"] = x
        map["y"] = y
        map["z"] = z
        return map
    }
}
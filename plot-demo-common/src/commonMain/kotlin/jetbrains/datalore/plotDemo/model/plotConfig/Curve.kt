/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.plotConfig

import jetbrains.datalore.plot.parsePlotSpec

class Curve {
    fun plotSpecList(): List<MutableMap<String, Any>> {
        return listOf(
            example()
        )
    }
    companion object {
        private fun example(): MutableMap<String, Any> {
            val spec = "{" +
                    "   'kind': 'plot'," +
                    "   'layers': [" +
                    "           {" +
                    "             'data': {'x': [-3,3], 'y': [-3,3]}," +
                    "             'mapping': { 'x': 'x', 'y': 'y'}," +
                    "             'geom': 'point'" +
                    "           }," +
                    "           {" +
                    "             'geom': 'curve'," +
                    "             'x': -2, 'y':  1, 'xend': 1, 'yend': -1, " +
                    "             'angle': 30," +
                    "             'curvature': -1," +
                    "             'ncp': 5," +
                    "             'color': 'red', 'tooltips': { 'lines': ['Tooltip'] }, " +
                    "             'size': 1" +
                    "           }," +
                    "           {" +
                    "             'geom': 'segment', 'x': -2, 'y':  1, 'xend': 1, 'yend': -1," +
                    "             'color': 'blue', 'alpha': 0.3," +
                    "             'size': 0.5" +
                    "           }" +
                    "         ]" +
                    "}"

            return HashMap(parsePlotSpec(spec))
        }
    }
}
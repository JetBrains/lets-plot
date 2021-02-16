/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.CoordinateSystem
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.render.SvgRoot

class StepGeom : LineGeom() {
    private var myDirection = DEF_DIRECTION

    fun setDirection(dir: String) {
        myDirection = Direction.toDirection(dir)
    }

    override fun buildIntern(root: SvgRoot, aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val dataPoints = dataPoints(aesthetics)
        val helper = LinesHelper(pos, coord, ctx)
        val pathInfos = helper.createSteps(dataPoints, myDirection)
        for (pathInfo in pathInfos) {
            root.add(pathInfo.path.rootGroup)
        }
    }

    enum class Direction {
        HV, VH;


        companion object {

            fun toDirection(str: String): Direction {
                return when (str) {
                    "hv", "HV" -> HV
                    "vh", "VH" -> VH
                    else -> throw IllegalArgumentException("Direction $str is not allowed, only accept 'hv' or 'vh'")
                }
            }
        }
    }

    companion object {
        // default
        val DEF_DIRECTION = Direction.HV
//        val RENDERS = LineGeom.RENDERS
        const  val HANDLES_GROUPS =
    LineGeom.HANDLES_GROUPS
    }
}

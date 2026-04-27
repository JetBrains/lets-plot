/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintsCollection.HintConfigFactory.HintConfig
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.cursorTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.horizontalTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.rotatedTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Companion.verticalTooltip
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipHint.Placement


class HintsCollection(private val myPoint: DataPointAesthetics, private val myHelper: GeomHelper) {
    private val _hints = HashMap<Aes<*>, TooltipHint>()

    val hints: Map<Aes<*>, TooltipHint>
        get() = _hints

    fun addHint(hintConfig: HintConfig): HintsCollection {
        val coord = getCoord(hintConfig)

        if (coord != null) {
            _hints[hintConfig.aes] = createHint(hintConfig, coord)
        }

        return this
    }

    private fun getCoord(hintConfig: HintConfig): DoubleVector? {
        if (hintConfig.baseCoord == null) {
            throw IllegalArgumentException("coord is not set")
        }

        val aes = hintConfig.aes
        if (!myPoint.defined(aes)) {
            return null
        }

        val coord = when {
            Aes.isPositionalX(aes) -> DoubleVector(myPoint.get(aes)!!, hintConfig.baseCoord!!)
            Aes.isPositionalY(aes) -> DoubleVector(hintConfig.baseCoord!!, myPoint.get(aes)!!)
            else -> throw IllegalStateException("Positional aes expected but was $aes.")
        }
        return myHelper.toClient(coord, myPoint)!!.let {
            if (myHelper.ctx.flipped) {
                it.flip()
            } else {
                it
            }
        }
    }

    private fun createHint(hintConfig: HintConfig, coord: DoubleVector): TooltipHint {
        val objectRadius = hintConfig.objectRadius
        val color = hintConfig.color

        if (objectRadius == null) {
            throw IllegalArgumentException("object radius is not set")
        }

        return when (hintConfig.placement) {
            Placement.VERTICAL -> verticalTooltip(coord, objectRadius, fillColor = color, markerColors = emptyList())
            Placement.HORIZONTAL -> horizontalTooltip(
                coord,
                objectRadius,
                fillColor = color,
                markerColors = emptyList()
            )

            Placement.CURSOR -> cursorTooltip(coord, markerColors = emptyList())
            Placement.ROTATED -> rotatedTooltip(coord, objectRadius, color)
            else -> throw IllegalArgumentException("Unknown hint kind: " + hintConfig.placement)
        }
    }

    class HintConfigFactory {

        private var myDefaultObjectRadius: Double? = null
        private var myDefaultBaseCoord: Double? = null
        private var myDefaultColor: Color? = null
        private var myDefaultPlacement: Placement? = null

        fun defaultObjectRadius(defaultObjectRadius: Double): HintConfigFactory {
            myDefaultObjectRadius = defaultObjectRadius
            return this
        }

        fun defaultCoord(defaultCoord: Double): HintConfigFactory {
            myDefaultBaseCoord = defaultCoord
            return this
        }

        fun defaultColor(v: Color, alpha: Double?): HintConfigFactory {
            myDefaultColor = if (alpha != null) {
                v.changeAlpha((255 * alpha).toInt())
            } else {
                v
            }
            return this
        }

        fun create(aes: Aes<Double>): HintConfig {
            require(Aes.isPositional(aes))
            return HintConfig(aes)
        }

        fun defaultKind(placement: Placement): HintConfigFactory {
            myDefaultPlacement = placement
            return this
        }

        inner class HintConfig internal constructor(val aes: Aes<Double>) {
            val placement: Placement?
            var objectRadius: Double? = null
                private set
            var baseCoord: Double? = null
                private set
            internal var color: Color? = null
                private set

            init {
                objectRadius = myDefaultObjectRadius
                baseCoord = myDefaultBaseCoord
                placement = myDefaultPlacement
                color = myDefaultColor
            }

            fun objectRadius(v: Double): HintConfig {
                objectRadius = v
                return this
            }

            fun color(v: Color): HintConfig {
                color = v
                return this
            }
        }
    }
}

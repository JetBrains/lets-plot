/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.HintsCollection.HintConfigFactory.HintConfig
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.cursorTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.horizontalTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.rotatedTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.verticalTooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Kind


class HintsCollection(private val myPoint: DataPointAesthetics, private val myHelper: GeomHelper) {
    private val myHints = HashMap<Aes<*>, TipLayoutHint>()

    val hints: MutableMap<Aes<*>, TipLayoutHint>
        get() = myHints

    fun addHint(hintConfig: HintConfig): HintsCollection {
        val coord = getCoord(hintConfig)

        if (coord != null) {
            hints[hintConfig.aes] = createHint(hintConfig, coord)
        }

        return this
    }

    private fun getCoord(hintConfig: HintConfig): DoubleVector? {
        if (hintConfig.x == null) {
            throw IllegalArgumentException("x coord is not set")
        }

        val aes = hintConfig.aes
        return if (!myPoint.defined(aes)) {
            null
        } else {
            myHelper.toClient(DoubleVector(hintConfig.x!!, myPoint.get(aes)!!), myPoint).let {
                if (myHelper.ctx.flipped) {
                    it.flip()
                } else {
                    it
                }
            }
        }

    }

    private fun createHint(hintConfig: HintConfig, coord: DoubleVector): TipLayoutHint {
        val objectRadius = hintConfig.objectRadius
        val color = hintConfig.color

        if (objectRadius == null) {
            throw IllegalArgumentException("object radius is not set")
        }

        return when (hintConfig.kind) {
            Kind.VERTICAL_TOOLTIP -> verticalTooltip(coord, objectRadius, markerColors = emptyList())
            Kind.HORIZONTAL_TOOLTIP -> horizontalTooltip(coord, objectRadius, markerColors = emptyList())
            Kind.CURSOR_TOOLTIP -> cursorTooltip(coord, markerColors = emptyList())
            Kind.ROTATED_TOOLTIP -> rotatedTooltip(coord, objectRadius, color)
            else -> throw IllegalArgumentException("Unknown hint kind: " + hintConfig.kind)
        }
    }

    class HintConfigFactory {

        private var myDefaultObjectRadius: Double? = null
        private var myDefaultX: Double? = null
        private var myDefaultColor: Color? = null
        private var myDefaultKind: Kind? = null

        fun defaultObjectRadius(defaultObjectRadius: Double): HintConfigFactory {
            myDefaultObjectRadius = defaultObjectRadius
            return this
        }

        fun defaultX(defaultX: Double): HintConfigFactory {
            myDefaultX = defaultX
            return this
        }

        fun defaultColor(v: Color, alpha: Double?): HintConfigFactory {
            if (alpha != null) {
                myDefaultColor = v.changeAlpha((255 * alpha).toInt())
            } else {
                myDefaultColor = v
            }
            return this
        }

        fun create(aes: Aes<Double>): HintConfig {
            return HintConfig(aes)
        }

        fun defaultKind(kind: Kind): HintConfigFactory {
            myDefaultKind = kind
            return this
        }

        inner class HintConfig internal constructor(val aes: Aes<Double>) {
            val kind: Kind?
            var objectRadius: Double? = null
                private set
            var x: Double? = null
                private set
            internal var color: Color? = null
                private set

            init {
                objectRadius = myDefaultObjectRadius
                x = myDefaultX
                kind = myDefaultKind
                color = myDefaultColor
            }

            fun objectRadius(v: Double): HintConfig {
                objectRadius = v
                return this
            }

            fun x(v: Double): HintConfig {
                x = v
                return this
            }

            fun color(v: Color): HintConfig {
                color = v
                return this
            }
        }
    }
}

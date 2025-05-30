/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.aes

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.commons.typedKey.TypedKeyHashMap
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_ALPHA
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape

class AestheticsDefaults private constructor(
    private val defaults: TypedKeyHashMap,
    private val defaultsInLegend: TypedKeyHashMap,
) {

    constructor(geomTheme: GeomTheme) : this(
        defaults = TypedKeyHashMap().apply {
            for (aes in Aes.values()) {
                // Safe cast because AesInitValue.get(aes) is guaranteed to return correct type.
                @Suppress("UNCHECKED_CAST")
                put(aes as Aes<Any>, AesInitValue[aes])
            }
            // defaults from geom theme:
            put(Aes.COLOR, geomTheme.color())
            put(Aes.FILL, geomTheme.fill())
            put(Aes.ALPHA, geomTheme.alpha())
            put(Aes.SIZE, geomTheme.size())
            put(Aes.LINEWIDTH, geomTheme.lineWidth())
            put(Aes.STROKE, geomTheme.lineWidth())
            put(Aes.POINT_SIZE, geomTheme.pointSize())
            put(Aes.SEGMENT_COLOR, geomTheme.segmentColor())
            put(Aes.SEGMENT_SIZE, geomTheme.segmentSize())
            put(Aes.SEGMENT_ALPHA, geomTheme.segmentAlpha())
        },
        defaultsInLegend = TypedKeyHashMap().apply {
            put(Aes.ALPHA, DEFAULT_ALPHA)
        }
    )

    private fun <T> update(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        defaults.put(aes, defaultValue)
        return this
    }

    private fun <T> updateInLegend(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        defaultsInLegend.put(aes, defaultValue)
        return this
    }

    fun <T> defaultValue(aes: Aes<T>): T {
        return defaults[aes]
    }

    fun <T> defaultValueInLegend(aes: Aes<T>): T {
        return if (defaultsInLegend.containsKey(aes)) {
            defaultsInLegend[aes]
        } else {
            defaultValue(aes)
        }
    }

    fun <T> with(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        return AestheticsDefaults(
            defaults = this.defaults.makeCopy().also { it.put(aes, defaultValue) },
            defaultsInLegend = this.defaultsInLegend
        )
    }

    companion object {
        private fun point(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        private fun bar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
        }

        private fun dotplot(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        private fun errorBar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.45)
                .update(Aes.HEIGHT, 0.45)
        }

        private fun crossBar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
                .update(Aes.Y, Double.NaN)
        }

        private fun pointRange(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.Y, Double.NaN)
        }

        private fun boxplot(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
        }

        private fun text(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
        }

        private fun textRepel(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
                .updateInLegend(Aes.POINT_SIZE, 5.0)
                .updateInLegend(Aes.SEGMENT_COLOR, geomTheme.segmentColor())
                .updateInLegend(Aes.SEGMENT_SIZE, geomTheme.segmentSize())
                .updateInLegend(Aes.SEGMENT_ALPHA, geomTheme.segmentAlpha())
        }

        private fun pie(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 1.0)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
                .updateInLegend(Aes.COLOR, geomTheme.pen())
        }

        private fun lollipop(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
                .update(Aes.SHAPE, NamedShape.STICK_CIRCLE)
        }

        private fun base(geomTheme: GeomTheme): AestheticsDefaults {
            return AestheticsDefaults(geomTheme)
        }

        fun create(geomKind: GeomKind, geomTheme: GeomTheme): AestheticsDefaults {
            return when (geomKind) {
                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.SINA,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2 -> point(geomTheme)

                GeomKind.BAR -> bar(geomTheme)

                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT -> dotplot(geomTheme)

                GeomKind.ERROR_BAR -> errorBar(geomTheme)

                GeomKind.CROSS_BAR -> crossBar(geomTheme)

                GeomKind.POINT_RANGE -> pointRange(geomTheme)

                GeomKind.BOX_PLOT -> boxplot(geomTheme)

                GeomKind.TEXT,
                GeomKind.LABEL -> text(geomTheme)
                GeomKind.TEXT_REPEL,
                GeomKind.LABEL_REPEL -> textRepel(geomTheme)

                GeomKind.PIE -> pie(geomTheme)

                GeomKind.LOLLIPOP -> lollipop(geomTheme)

                GeomKind.BLANK,
                GeomKind.PATH,
                GeomKind.LINE,
                GeomKind.SMOOTH,
                GeomKind.HISTOGRAM,
                GeomKind.TILE,
                GeomKind.BIN_2D,
                GeomKind.HEX,
                GeomKind.MAP,
                GeomKind.LINE_RANGE,
                GeomKind.POLYGON,
                GeomKind.AB_LINE,
                GeomKind.H_LINE,
                GeomKind.V_LINE,
                GeomKind.BAND,
                GeomKind.AREA_RIDGES,
                GeomKind.VIOLIN,
                GeomKind.RIBBON,
                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.CONTOUR,
                GeomKind.CONTOURF,
                GeomKind.DENSITY2D,
                GeomKind.DENSITY2DF,
                GeomKind.Q_Q_LINE,
                GeomKind.Q_Q_2_LINE,
                GeomKind.FREQPOLY,
                GeomKind.RECT,
                GeomKind.SEGMENT,
                GeomKind.CURVE,
                GeomKind.SPOKE,
                GeomKind.STEP,
                GeomKind.RASTER,
                GeomKind.IMAGE,
                GeomKind.LIVE_MAP -> base(geomTheme)
            }
        }
    }
}

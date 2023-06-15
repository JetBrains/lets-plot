/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.render.point.NamedShape

open class AestheticsDefaults(geomTheme: GeomTheme) {

    private val myDefaults = TypedKeyHashMap().apply {
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
    }
    private val myDefaultsInLegend = TypedKeyHashMap()

    private fun <T> update(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        myDefaults.put(aes, defaultValue)
        return this
    }

    private fun <T> updateInLegend(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        myDefaultsInLegend.put(aes, defaultValue)
        return this
    }

    fun <T> defaultValue(aes: Aes<T>): T {
        return myDefaults[aes]
    }

    fun <T> defaultValueInLegend(aes: Aes<T>): T {
        return if (myDefaultsInLegend.containsKey(aes)) {
            myDefaultsInLegend[aes]
        } else {
            defaultValue(aes)
        }
    }

    companion object {
        private fun point(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        private fun path(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun line(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun abline(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun hline(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun vline(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun smooth(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun bar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
        }

        private fun histogram(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun dotplot(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        private fun tile(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun bin2d(geomTheme: GeomTheme): AestheticsDefaults {
            return tile(geomTheme)
        }

        private fun errorBar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.45)
                .update(Aes.HEIGHT, 0.45)
        }

        private fun crossBar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
        }

        private fun lineRange(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun pointRange(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun polygon(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun map(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun boxplot(geomTheme: GeomTheme): AestheticsDefaults {
            return crossBar(geomTheme)
        }

        private fun areaRidges(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun violin(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun ydotplot(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        private fun livemap(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun ribbon(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun area(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun density(geomTheme: GeomTheme): AestheticsDefaults {
            return area(geomTheme)
        }

        private fun contour(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun contourf(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun density2d(geomTheme: GeomTheme): AestheticsDefaults {
            return contour(geomTheme)
        }

        private fun density2df(geomTheme: GeomTheme): AestheticsDefaults {
            return contourf(geomTheme)
        }

        private fun jitter(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
        }

        private fun qq(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
        }

        private fun qq2(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
        }

        private fun qq_line(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun qq2_line(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun freqpoly(geomTheme: GeomTheme): AestheticsDefaults {
            return area(geomTheme)
        }

        private fun step(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun rect(geomTheme: GeomTheme): AestheticsDefaults {
            return polygon(geomTheme)
        }

        private fun segment(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        private fun text(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
        }

        private fun label(geomTheme: GeomTheme): AestheticsDefaults {
            return text(geomTheme)
        }

        private fun raster(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun image(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        private fun pie(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 1.0)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
                .updateInLegend(Aes.COLOR, Color.TRANSPARENT)
        }

        private fun lollipop(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
                .update(Aes.SHAPE, NamedShape.STICK_CIRCLE)
                .update(Aes.STROKE, 1.0)
        }

        private fun base(geomTheme: GeomTheme): AestheticsDefaults {
            return AestheticsDefaults(geomTheme)
        }

        fun create(geomKind: GeomKind, geomTheme: GeomTheme): AestheticsDefaults {
            val creator = when (geomKind) {
                GeomKind.PATH -> ::path
                GeomKind.LINE -> ::line
                GeomKind.SMOOTH -> ::smooth
                GeomKind.BAR -> ::bar
                GeomKind.HISTOGRAM -> ::histogram
                GeomKind.DOT_PLOT -> ::dotplot
                GeomKind.TILE -> ::tile
                GeomKind.BIN_2D -> ::bin2d
                GeomKind.MAP -> ::map
                GeomKind.ERROR_BAR -> ::errorBar
                GeomKind.CROSS_BAR -> ::crossBar
                GeomKind.LINE_RANGE -> ::lineRange
                GeomKind.POINT_RANGE -> ::pointRange
                GeomKind.POLYGON -> ::polygon
                GeomKind.AB_LINE -> ::abline
                GeomKind.H_LINE -> ::hline
                GeomKind.V_LINE -> ::vline
                GeomKind.BOX_PLOT -> ::boxplot
                GeomKind.AREA_RIDGES -> ::areaRidges
                GeomKind.VIOLIN -> ::violin
                GeomKind.Y_DOT_PLOT -> ::ydotplot
                GeomKind.LIVE_MAP -> ::livemap
                GeomKind.POINT -> ::point
                GeomKind.RIBBON -> ::ribbon
                GeomKind.AREA -> ::area
                GeomKind.DENSITY -> ::density
                GeomKind.CONTOUR -> ::contour
                GeomKind.CONTOURF -> ::contourf
                GeomKind.DENSITY2D -> ::density2d
                GeomKind.DENSITY2DF -> ::density2df
                GeomKind.JITTER -> ::jitter
                GeomKind.Q_Q -> ::qq
                GeomKind.Q_Q_2 -> ::qq2
                GeomKind.Q_Q_LINE -> ::qq_line
                GeomKind.Q_Q_2_LINE -> ::qq2_line
                GeomKind.FREQPOLY -> ::freqpoly
                GeomKind.STEP -> ::step
                GeomKind.RECT -> ::rect
                GeomKind.SEGMENT -> ::segment
                GeomKind.TEXT -> ::text
                GeomKind.LABEL -> ::label
                GeomKind.RASTER -> ::raster
                GeomKind.IMAGE -> ::image
                GeomKind.PIE -> ::pie
                GeomKind.LOLLIPOP -> ::lollipop
            }
            return creator(geomTheme)
        }
    }
}

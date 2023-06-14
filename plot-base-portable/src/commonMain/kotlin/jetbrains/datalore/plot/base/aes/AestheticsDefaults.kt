/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.aes

import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
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
        fun point(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        fun path(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun line(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun abline(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun hline(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun vline(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun smooth(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun bar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
        }

        fun histogram(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun dotplot(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        fun tile(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun bin2d(geomTheme: GeomTheme): AestheticsDefaults {
            return tile(geomTheme)
        }

        fun errorBar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.45)
                .update(Aes.HEIGHT, 0.45)
        }

        fun crossBar(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .update(Aes.WIDTH, 0.9)
        }

        fun lineRange(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun pointRange(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun polygon(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun map(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun boxplot(geomTheme: GeomTheme): AestheticsDefaults {
            return crossBar(geomTheme)
        }

        fun areaRidges(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun violin(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun ydotplot(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 5.0)
        }

        fun livemap(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun ribbon(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun area(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun density(geomTheme: GeomTheme): AestheticsDefaults {
            return area(geomTheme)
        }

        fun contour(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun contourf(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun density2d(geomTheme: GeomTheme): AestheticsDefaults {
            return contour(geomTheme)
        }

        fun density2df(geomTheme: GeomTheme): AestheticsDefaults {
            return contourf(geomTheme)
        }

        fun jitter(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
        }

        fun qq(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
        }

        fun qq2(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
        }

        fun qq_line(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun qq2_line(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun freqpoly(geomTheme: GeomTheme): AestheticsDefaults {
            return area(geomTheme)
        }

        fun step(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun rect(geomTheme: GeomTheme): AestheticsDefaults {
            return polygon(geomTheme)
        }

        fun segment(geomTheme: GeomTheme): AestheticsDefaults {
            return path(geomTheme)
        }

        fun text(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
        }

        fun label(geomTheme: GeomTheme): AestheticsDefaults {
            return text(geomTheme)
        }

        fun raster(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun image(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
        }

        fun pie(geomTheme: GeomTheme): AestheticsDefaults {
            return base(geomTheme)
                .updateInLegend(Aes.SIZE, 1.0)
                .updateInLegend(Aes.FILL, Color.TRANSPARENT)
                .updateInLegend(Aes.COLOR, Color.TRANSPARENT)
        }

        fun lollipop(geomTheme: GeomTheme): AestheticsDefaults {
            return point(geomTheme)
                .update(Aes.SHAPE, NamedShape.STICK_CIRCLE)
                .update(Aes.STROKE, 1.0)
        }

        private fun base(geomTheme: GeomTheme): AestheticsDefaults {
            return AestheticsDefaults(geomTheme)
        }
    }
}

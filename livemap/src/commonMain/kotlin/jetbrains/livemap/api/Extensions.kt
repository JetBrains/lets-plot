/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.api

import jetbrains.datalore.base.values.Color

fun Polygons.polygon(block: PolygonsBuilder.() -> Unit) {
    items.add(
        PolygonsBuilder().apply {
            index = 0
            mapId = ""
            regionId = ""

            lineDash = emptyList()
            strokeColor = Color.BLACK
            strokeWidth = 0.0
            fillColor = Color.GREEN
            coordinates = null
        }
            .apply(block)
            .build()
    )
}

fun Lines.line(block: LineBuilder.() -> Unit) {
    items.add(
        LineBuilder()
            .apply {
                index = 0
                mapId = ""
                regionId = ""

                lineDash = emptyList()
                strokeColor = Color.BLACK
                strokeWidth = 1.0

            }
            .apply(block)
            .build()
    )
}

fun Bars.bar(block: ChartSource.() -> Unit) {
    factory.add(ChartSource().apply(block))
}

fun Pies.pie(block: ChartSource.() -> Unit) {
    factory.add(ChartSource().apply(block))
}

fun Texts.text(block: TextBuilder.() -> Unit) {
    items.add(
        TextBuilder()
            .apply(block)
            .build()
    )
}

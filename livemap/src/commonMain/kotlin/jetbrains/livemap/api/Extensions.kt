package jetbrains.livemap.api

import jetbrains.datalore.base.values.Color

fun Points.point(block: PointBuilder.() -> Unit) {
    items.add(
        PointBuilder().apply {
            animation = 0
            index = 0
            mapId = ""
            regionId = ""
            label = ""

            strokeWidth = 1.0
            strokeColor = Color.BLACK

            fillColor = Color.WHITE

            radius = 4.0
            shape = 1
        }
            .apply(block)
            .build()
    )
}

fun Paths.path(block: PathBuilder.() -> Unit) {
    items.add(
        PathBuilder().apply {
            index = 0
            mapId = ""
            regionId = ""

            lineDash = emptyList()
            strokeColor = Color.BLACK
            strokeWidth = 1.0
            coordinates = emptyList()

            animation = 0
            speed = 0.0
            flow = 0.0

        }
            .apply(block)
            .build()
    )
}

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
            coordinates = emptyList()
        }
            .apply(block)
            .build()
    )
}

fun Lines.line(block: LineBuilder.() -> Unit) {
    items.add(
        LineBuilder().apply {
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

fun Bars.bar(block: BarSource.() -> Unit) {
    factory.add(BarSource().apply(block))
}

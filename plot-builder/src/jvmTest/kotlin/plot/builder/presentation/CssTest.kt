package jetbrains.datalore.plot.builder.presentation

import kotlin.test.Test
import kotlin.test.assertEquals

class CssTest {
    object Constants {
    }

    private val cssTest =
""".simpleselector {
    font-family: some-font;
}
text {
    font-size: 5px;
    fill: #000000;
}
.asd {
    cursor: crosshair;
}
.outer text {
    font-size: 7px;
}
.outer line {
    shape-rendering: crispedges;
}
.highlight {
    fill-opacity: 0.75;
}
"""

    @Test
    fun cssCompare() {
        val cssResource = CssResourceBuilder()
            .add(SelectorBuilder("simpleselector")
                .fontFamily("some-font")
            )
            .add(SelectorBuilder(SelectorType.TEXT)
                .fontSize(5, SizeMeasure.PX)
                .fill("#000000")
            )
            .add(SelectorBuilder("asd")
                .cursor(CursorValue.CROSSHAIR)
            )
            .add(SelectorBuilder("outer")
                .innerSelector(SelectorType.TEXT)
                .fontSize(7, SizeMeasure.PX)
            )
            .add(SelectorBuilder("outer").innerSelector(SelectorType.LINE)
                .shapeRendering(ShapeRenderingValue.CRISPEDGES)
            )
            .add(SelectorBuilder("highlight")
                .fillOpacity(0.75f)
            )
            .build()
        val cssString = cssResource.toString().replace("\t", "    ")

        assertEquals(cssTest, cssString)
    }
}
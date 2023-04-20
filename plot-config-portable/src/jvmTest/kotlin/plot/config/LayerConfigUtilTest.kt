/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.Option.GeomName
import kotlin.test.Test
import kotlin.test.assertEquals

class LayerConfigUtilTest {

    @Test
    fun test() {
        doCheck(GeomName.POINT, emptyMap(), mapOf("name" to "identity"))
        doCheck(GeomName.POINT, withPos("dodge"), mapOf("name" to "dodge"))
        doCheck(GeomName.POINT, withPos(dodge()), mapOf("name" to "dodge"))
        doCheck(GeomName.POINT, withPos(dodge(1.23)), mapOf("name" to "dodge", "width" to 1.23))

        doCheck(GeomName.BOX_PLOT, emptyMap(), mapOf("name" to "dodge", "width" to 0.95))
        doCheck(GeomName.BOX_PLOT, withPos("dodge"), mapOf("name" to "dodge", "width" to 0.95))
        doCheck(GeomName.BOX_PLOT, withPos(dodge()), mapOf("name" to "dodge", "width" to 0.95))
        doCheck(GeomName.BOX_PLOT, withPos(dodge(1.23)), mapOf("name" to "dodge", "width" to 1.23))
        doCheck(GeomName.BOX_PLOT, withPos("jitter"), mapOf("name" to "jitter"))
        doCheck(GeomName.BOX_PLOT, withPos(jitter()), mapOf("name" to "jitter"))
        doCheck(
            GeomName.BOX_PLOT,
            withPos(jitter(1.01, 2.02)),
            mapOf("name" to "jitter", "width" to 1.01, "height" to 2.02)
        )

        doCheck(GeomName.JITTER, emptyMap(), mapOf("name" to "jitter"))
        doCheck(
            GeomName.JITTER,
            mapOf("width" to 1.01, "height" to 2.02),
            mapOf("name" to "jitter", "width" to 1.01, "height" to 2.02)
        )
        doCheck(
            GeomName.JITTER,
            withPos(jitter(width = 0.01, height = 0.02)) + mapOf("width" to 1.01, "height" to 2.02),
            mapOf("name" to "jitter", "width" to 1.01, "height" to 2.02)
        )
        doCheck(
            GeomName.JITTER,
            withPos(jitter(width = 1.01)) + mapOf("height" to 2.02),
            mapOf("name" to "jitter", "width" to 1.01, "height" to 2.02)
        )

        doCheck(GeomName.Y_DOT_PLOT, emptyMap(), mapOf("name" to "dodge", "width" to 0.95))
        doCheck(GeomName.Y_DOT_PLOT, mapOf("stackgroups" to true), mapOf("name" to "identity"))

        doCheck(GeomName.TEXT, emptyMap(), mapOf("name" to "identity"))
        doCheck(GeomName.TEXT, mapOf("nudge_x" to 0.2), mapOf("name" to "nudge", "x" to 0.2))
        doCheck(
            GeomName.TEXT,
            withPos("identity") + mapOf("nudge_x" to 0.2),
            mapOf("name" to "nudge", "x" to 0.2)
        )
        doCheck(
            GeomName.TEXT,
            withPos(nudge(x = 1.0)) + mapOf("nudge_x" to 0.2),
            mapOf("name" to "nudge", "x" to 0.2)
        )
        doCheck(
            GeomName.TEXT,
            withPos(nudge(x = 0.2)) + mapOf("nudge_y" to 0.5),
            mapOf("name" to "nudge", "x" to 0.2, "y" to 0.5)
        )
    }

    private fun doCheck(geom: String, layerOptions: Map<String, Any>, expectedPosOptions: Map<String, Any>) {
        val geomKind = GeomName.toGeomKind(geom)
        val geomProto = GeomProto(geomKind)
        val defaultOptions = geomProto.defaultOptions()
        val posOptions = LayerConfigUtil.positionAdjustmentOptions(
            OptionsAccessor(layerOptions + mapOf(Option.Layer.GEOM to geom), defaultOptions),
            geomProto
        )

        assertEquals(expectedPosOptions, posOptions, "'$geom':")
    }

    private fun withPos(pos: Any): Map<String, Any> {
        return mapOf("position" to pos)
    }

    private fun dodge(width: Double? = null): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return mapOf("name" to "dodge", "width" to width).filterValues { it != null } as Map<String, Any>
    }

    private fun jitter(width: Double? = null, height: Double? = null): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return mapOf(
            "name" to "jitter",
            "width" to width,
            "height" to height,
        ).filterValues { it != null } as Map<String, Any>
    }

    private fun nudge(x: Double? = null, y: Double? = null): Map<String, Any> {
        @Suppress("UNCHECKED_CAST")
        return mapOf("name" to "nudge", "x" to x, "y" to y).filterValues { it != null } as Map<String, Any>
    }
}

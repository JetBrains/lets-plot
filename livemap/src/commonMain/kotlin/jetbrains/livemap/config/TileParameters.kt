/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.config

import jetbrains.gis.tileprotocol.TileService.Theme

class TileParameters(tiles: Map<*, *>) {
    val raster: String? = tiles["raster"] as String?
    val vector: VectorTiles = readVector(tiles["vector"])

    private fun readVector(params: Any?): VectorTiles {
        val vector = VectorTiles()

        return when(val v = params) {
            null -> vector
            is Map<*, *> -> vector.apply {
                v["host"]?.let { if (it is String) host = it }
                v["port"]?.let { if (it is Int) port = it }
                v["theme"]?.let { if (it is String) theme = parseTheme(it) }
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun parseTheme(theme: String): Theme {
        try {
            return Theme.valueOf(theme.toUpperCase())
        } catch (ignored: Exception) {
            throw IllegalArgumentException("Unknown theme type: $theme")
        }
    }
}

class VectorTiles {
    var host: String = "tiles.datalore.io"
    var port: Int? = null
    var theme: Theme = Theme.COLOR
}
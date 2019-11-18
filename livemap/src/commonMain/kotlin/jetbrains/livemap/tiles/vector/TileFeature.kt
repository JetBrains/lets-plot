/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.tiles.vector

import jetbrains.datalore.base.projectionGeometry.Geometry
import jetbrains.gis.tileprotocol.mapConfig.TilePredicate
import jetbrains.livemap.projections.Client
import jetbrains.livemap.tiles.vector.TileFeature.FieldName.CLASS
import jetbrains.livemap.tiles.vector.TileFeature.FieldName.SUB


class TileFeature(
    val tileGeometry: Geometry<Client>,
    private val myKind: Int?,
    private val mySub: Int?,
    val label: String?,
    val short: String?
) : TilePredicate {

    override fun getFieldValue(key: String): Int = when {
        SUB.field.equals(key, ignoreCase = true) -> mySub ?: error("sub is empty")
        CLASS.field.equals(key, ignoreCase = true) -> myKind ?: error("kind is empty")
        else -> error("Unknown myKey kind: $key")
    }

    enum class FieldName (val field: String) {
        CLASS("class"),
        SUB("sub")
    }
}

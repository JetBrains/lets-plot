/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol.json

enum class RequestTypes private constructor(private val myValue: String) {
    CONFIGURE_CONNECTION("configureConnection"),
    GET_BINARY_TILE("getBinaryTile"),
    CANCEL_BINARY_TILE("cancelBinaryTile");

    override fun toString(): String {
        return myValue
    }
}

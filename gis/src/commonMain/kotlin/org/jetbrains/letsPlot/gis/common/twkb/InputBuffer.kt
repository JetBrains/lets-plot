/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.common.twkb

internal class InputBuffer(
    private val myData: ByteArray
) {
    private var myPointer = 0

    operator fun hasNext() = myPointer < myData.size

    fun readByte() = myData[myPointer++].toInt()
    fun readVarInt() = org.jetbrains.letsPlot.gis.common.twkb.VarInt.readVarInt(this::readByte)
    fun readVarUInt() = org.jetbrains.letsPlot.gis.common.twkb.VarInt.readVarUInt(this::readByte)
}

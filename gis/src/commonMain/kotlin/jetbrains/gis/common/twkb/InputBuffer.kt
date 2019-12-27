/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.common.twkb

internal class InputBuffer(
    private val myData: ByteArray
) {
    private var myPointer = 0

    operator fun hasNext() = myPointer < myData.size

    fun readByte() = myData[myPointer++].toInt()
    fun readVarInt() = VarInt.readVarInt(this::readByte)
    fun readVarUInt() = VarInt.readVarUInt(this::readByte)
}

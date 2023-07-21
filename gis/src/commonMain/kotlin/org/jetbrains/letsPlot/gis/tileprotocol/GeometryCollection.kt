/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.gis.tileprotocol

open class GeometryCollection(private val myTwkb: ByteArray) {

    companion object {
        fun createEmpty(): GeometryCollection {
            return GeometryCollection(ByteArray(0))
        }
    }

    fun asTwkb(): ByteArray {
        return myTwkb
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as GeometryCollection

        if (!myTwkb.contentEquals(other.myTwkb)) return false

        return true
    }

    override fun hashCode(): Int {
        return myTwkb.contentHashCode()
    }

    override fun toString(): String {
        return "GeometryCollection(myTwkb=$myTwkb)"
    }
}

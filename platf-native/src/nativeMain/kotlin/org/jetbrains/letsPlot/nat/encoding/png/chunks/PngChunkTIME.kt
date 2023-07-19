/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 *
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 *
 * THE FOLLOWING IS THE COPYRIGHT OF THE ORIGINAL DOCUMENT:
 *
 * Copyright (c) 2009-2012, Hernán J. González.
 * Licensed under the Apache License, Version 2.0.
 *
 * The original PNGJ library is written in Java and can be found here: [PNGJ](https://github.com/leonbloy/pngj).
 */

@file:Suppress("unused")
package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.commons.formatting.datetime.DateTimeFormat
import org.jetbrains.letsPlot.commons.intern.datetime.Date
import org.jetbrains.letsPlot.commons.intern.datetime.DateTime
import org.jetbrains.letsPlot.commons.intern.datetime.Month
import org.jetbrains.letsPlot.commons.intern.datetime.Time
import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.PngHelperInternal
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * tIME chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11tIME
 */
class PngChunkTIME(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11tIME
    private var dateTime: DateTime? = null

    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NONE

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(7, true)
        PngHelperInternal.writeInt2tobytes(dateTime?.year ?: 0, c.data, 0)
        c.data!![2] = ((dateTime?.month?.ordinal() ?: 0) + 1).toByte()
        c.data!![3] = dateTime?.day?.toByte() ?: 0.toByte()
        c.data!![4] = dateTime?.hours?.toByte() ?: 0.toByte()
        c.data!![5] = dateTime?.minutes?.toByte() ?: 0.toByte()
        c.data!![6] = dateTime?.seconds?.toByte() ?: 0.toByte()
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 7) throw PngjException("bad chunk $chunk")
        setYMDHMS(
            PngHelperInternal.readInt2fromBytes(chunk.data, 0),
            PngHelperInternal.readInt1fromByte(chunk.data, 2),
            PngHelperInternal.readInt1fromByte(chunk.data, 3),
            PngHelperInternal.readInt1fromByte(chunk.data, 4),
            PngHelperInternal.readInt1fromByte(chunk.data, 5),
            PngHelperInternal.readInt1fromByte(chunk.data, 6)
        )
    }

    fun setNow(now: DateTime) {
        dateTime = now
    }

    fun setYMDHMS(yearx: Int, monx: Int, dayx: Int, hourx: Int, minx: Int, secx: Int) {
        dateTime = DateTime(Date(dayx, Month.values()[monx - 1], yearx), Time(hourx, minx, secx))
    }

    val yMDHMS: IntArray
        get() = dateTime?.let { intArrayOf(it.year, it.month.ordinal() + 1, it.day, it.hours, it.minutes, it.seconds) } ?: IntArray(6)

    /** format YYYY/MM/DD HH:mm:SS  */
    val asString: String
        get() = dateTime?.let(DateTimeFormat("%Y/%m/%d %H:%M:%S")::apply) ?: "--/--/-- --:--:--"

    companion object {
        const val ID: String = ChunkHelper.tIME
    }
}
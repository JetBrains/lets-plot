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

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.PngHelperInternal
import org.jetbrains.letsPlot.nat.encoding.png.PngjException

/**
 * cHRM chunk.
 *
 *
 * see http://www.w3.org/TR/PNG/#11cHRM
 */
internal class PngChunkCHRM(info: ImageInfo?) : PngChunkSingle(ID, info) {
    // http://www.w3.org/TR/PNG/#11cHRM
    private var whitex = 0.0
    private var whitey = 0.0
    private var redx = 0.0
    private var redy = 0.0
    private var greenx = 0.0
    private var greeny = 0.0
    private var bluex = 0.0
    private var bluey = 0.0
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT

    override fun createRawChunk(): ChunkRaw {
        val c: ChunkRaw = createEmptyChunk(32, true)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(whitex), c.data, 0)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(whitey), c.data, 4)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(redx), c.data, 8)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(redy), c.data, 12)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(greenx), c.data, 16)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(greeny), c.data, 20)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(bluex), c.data, 24)
        PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(bluey), c.data, 28)
        return c
    }

    override fun parseFromRaw(chunk: ChunkRaw) {
        if (chunk.len != 32) throw PngjException("bad chunk $chunk")
        whitex = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 0))
        whitey = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 4))
        redx = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 8))
        redy = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 12))
        greenx = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 16))
        greeny = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 20))
        bluex = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 24))
        bluey = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(chunk.data, 28))
    }

    fun setChromaticities(
        whitex: Double, whitey: Double, redx: Double, redy: Double, greenx: Double, greeny: Double,
        bluex: Double, bluey: Double
    ) {
        this.whitex = whitex
        this.redx = redx
        this.greenx = greenx
        this.bluex = bluex
        this.whitey = whitey
        this.redy = redy
        this.greeny = greeny
        this.bluey = bluey
    }

    val chromaticities: DoubleArray
        get() = doubleArrayOf(whitex, whitey, redx, redy, greenx, greeny, bluex, bluey)

    companion object {
        const val ID: String = ChunkHelper.cHRM
    }
}
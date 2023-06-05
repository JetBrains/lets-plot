/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo

/**
 * PNG chunk type (abstract) that does not allow multiple instances in same
 * image.
 */
abstract class PngChunkSingle protected constructor(id: String, imgInfo: ImageInfo?) : PngChunk(id, imgInfo) {
    override fun allowsMultiple(): Boolean {
        return false
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + id.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false
        other as PngChunkSingle
        if (id != other.id) return false
        return true
    }
}
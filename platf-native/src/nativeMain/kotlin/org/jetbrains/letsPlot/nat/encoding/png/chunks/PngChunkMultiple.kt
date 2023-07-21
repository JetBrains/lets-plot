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

package org.jetbrains.letsPlot.nat.encoding.png.chunks

import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo


/**
 * PNG chunk type (abstract) that allows multiple instances in same image.
 */
abstract class PngChunkMultiple protected constructor(id: String, imgInfo: ImageInfo?) : PngChunk(id, imgInfo) {
    override fun allowsMultiple(): Boolean {
        return true
    }
    /**
     * NOTE: this chunk uses the default Object's equals() hashCode()
     * implementation.
     *
     * This is the right thing to do, normally.
     *
     * This is important, eg see ChunkList.removeFromList()
     */
}
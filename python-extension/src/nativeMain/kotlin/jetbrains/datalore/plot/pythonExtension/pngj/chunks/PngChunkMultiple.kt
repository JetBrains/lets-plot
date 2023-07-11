/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

import jetbrains.datalore.plot.pythonExtension.pngj.ImageInfo


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
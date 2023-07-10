/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.pythonExtension.pngj.chunks

/**
 * Decides if another chunk "matches", according to some criterion
 */
internal interface ChunkPredicate {
    /**
     * The other chunk matches with this one
     *
     * @param chunk
     * @return true if match
     */
    fun match(chunk: PngChunk): Boolean
}
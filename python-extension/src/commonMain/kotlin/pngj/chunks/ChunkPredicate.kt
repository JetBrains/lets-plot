/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj.chunks

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
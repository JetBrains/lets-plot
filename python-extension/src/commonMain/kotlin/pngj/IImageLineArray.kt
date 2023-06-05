/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

package org.jetbrains.letsPlot.util.pngj

/**
 * This interface is just for the sake of unifying some methods of
 * [ImageLineHelper] that can use both [ImageLineInt] or
 * [ImageLineByte]. It's not very useful outside that, and the user should
 * not rely much on this.
 */
internal interface IImageLineArray {
    val imageInfo: ImageInfo
    val filterType: FilterType

    /**
     * length of array (should correspond to samples)
     */
    val size: Int

    /**
     * Get i-th element of array (for 0 to size-1). The meaning of this is type
     * dependent. For ImageLineInt and ImageLineByte is the sample value.
     */
    fun getElem(i: Int): Int
}
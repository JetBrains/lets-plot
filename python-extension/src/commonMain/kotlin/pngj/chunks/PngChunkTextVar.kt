/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 * 
 * This file has been modified by JetBrains : Java code has been converted to Kotlin code.
 * */

@file:Suppress("unused")
package org.jetbrains.letsPlot.util.pngj.chunks

import org.jetbrains.letsPlot.util.pngj.ImageInfo

/**
 * Superclass (abstract) for three textual chunks (TEXT, ITXT, ZTXT)
 */
abstract class PngChunkTextVar protected constructor(id: String, info: ImageInfo?) : PngChunkMultiple(id, info) {
    var key // key/val: only for tEXt. lazy computed
            : String? = null
        protected set
    var value: String? = null
        protected set
    override val orderingConstraint: ChunkOrderingConstraint
        get() = ChunkOrderingConstraint.NONE

    class PngTxtInfo {
        var title: String? = null
        var author: String? = null
        var description: String? = null
        var creation_time // = (new Date()).toString();
                : String? = null
        var software: String? = null
        var disclaimer: String? = null
        var warning: String? = null
        var source: String? = null
        var comment: String? = null
    }

    fun setKeyVal(key: String?, value: String?) {
        this.key = key
        this.value = value
    }

    companion object {
        // http://www.w3.org/TR/PNG/#11keywords
        const val KEY_Title = "Title" // Short (one line) title or caption for image
        const val KEY_Author = "Author" // Name of image's creator
        const val KEY_Description = "Description" // Description of image (possibly long)
        const val KEY_Copyright = "Copyright" // Copyright notice
        const val KEY_Creation_Time = "Creation Time" // Time of original image creation
        const val KEY_Software = "Software" // Software used to create the image
        const val KEY_Disclaimer = "Disclaimer" // Legal disclaimer
        const val KEY_Warning = "Warning" // Warning of nature of content
        const val KEY_Source = "Source" // Device used to create the image
        const val KEY_Comment = "Comment" // Miscellaneous comment
    }
}
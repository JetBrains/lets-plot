/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

class SvgIRI(private val myElementId: String) {

    override fun toString(): String {
        return "url(#$myElementId)"
    }
}
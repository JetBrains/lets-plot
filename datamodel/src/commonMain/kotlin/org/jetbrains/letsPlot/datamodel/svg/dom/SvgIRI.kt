/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

class SvgIRI(private val myElementId: String) {

    override fun toString(): String {
        return "url(#$myElementId)"
    }
}
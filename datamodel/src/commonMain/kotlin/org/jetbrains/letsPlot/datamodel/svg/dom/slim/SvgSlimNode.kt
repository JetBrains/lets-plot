/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

interface SvgSlimNode {
    val elementName: String
    val attributes: Iterable<Attr>
    val slimChildren: Iterable<SvgSlimNode>

    interface Attr {
        val key: String
        val value: String
    }
}

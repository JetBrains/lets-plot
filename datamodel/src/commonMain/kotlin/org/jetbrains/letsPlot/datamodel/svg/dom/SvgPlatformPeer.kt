/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector

interface SvgPlatformPeer {
    fun getComputedTextLength(node: SvgTextContent): Double

    fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector

    fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector

    fun getBBox(element: SvgLocatable): DoubleRectangle
}
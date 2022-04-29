/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.StyleRenderer

interface SvgPlatformPeer {
    fun getComputedTextLength(node: SvgTextContent): Double

    fun invertTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector

    fun applyTransform(relative: SvgLocatable, point: DoubleVector): DoubleVector

    fun getBBox(element: SvgLocatable): DoubleRectangle

    fun applyStyleRenderer(styleRenderer: StyleRenderer)
}
/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.core.plot.base.render.text.LatexVectorClasses
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgStylableElement
import org.jetbrains.letsPlot.raster.scene.Path

internal class SvgPathElementMapper(
    source: SvgPathElement,
    target: Path,
    peer: SvgCanvasPeer
) : SvgElementMapper<SvgPathElement, Path>(source, target, peer) {

    override fun applyStyle() {
        // Fill-less vector LaTeX glyph paths inherit theme/stylesheet text color here.
        if (source.getAttribute(SvgShape.FILL).get() != null) return
        if (!hasLatexFormulaAncestor(source)) return
        val color = resolveInheritedTextColor(source, peer.styleSheet) ?: return
        target.fill = color
    }

    private fun hasLatexFormulaAncestor(source: SvgNode): Boolean {
        var node: SvgNode? = source.parent().get()
        while (node != null) {
            if (node is SvgStylableElement &&
                node.fullClass().split(' ').contains(LatexVectorClasses.FORMULA_CLASS)
            ) {
                return true
            }
            node = node.parent().get()
        }
        return false
    }
}

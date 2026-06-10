/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.mapping.svg

import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgShape
import org.jetbrains.letsPlot.raster.scene.Path

internal class SvgPathElementMapper(
    source: SvgPathElement,
    target: Path,
    peer: SvgCanvasPeer
) : SvgElementMapper<SvgPathElement, Path>(source, target, peer) {

    override fun applyStyle() {
        // Vector LaTeX glyph paths render with no baked fill (see Latex.kt) so they can inherit the
        // effective theme/stylesheet text color, applied here via the class on an enclosing line
        // element. Paths that carry an explicit fill — geom_text/label glyphs (programmatic color) and
        // the fill="none" LaTeX bbox guide — are left untouched; their FILL attribute is applied later
        // and would override anything set here anyway.
        if (source.getAttribute(SvgShape.FILL).get() != null) return
        val color = resolveInheritedTextColor(source, peer.styleSheet) ?: return
        target.fill = color
    }
}

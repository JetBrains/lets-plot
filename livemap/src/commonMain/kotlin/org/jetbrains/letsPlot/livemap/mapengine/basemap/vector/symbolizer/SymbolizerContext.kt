package org.jetbrains.letsPlot.livemap.mapengine.basemap.vector.symbolizer

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle

internal class SymbolizerContext {
    fun pinLabel(bbox: DoubleRectangle) {
        myLabelBounds.add(bbox)
    }

    fun intersectsAnyLabel(bbox: DoubleRectangle): Boolean {
        for (labelBounds in myLabelBounds) {
            if (labelBounds.intersects(bbox)) {
                return true
            }
        }
        return false
    }

    private val myLabelBounds = mutableListOf<DoubleRectangle>()
}
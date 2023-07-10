/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.awt.canvas

import org.jetbrains.letsPlot.commons.geometry.Vector
import java.awt.Rectangle
import javax.swing.JLayeredPane

class CanvasContainerPanel(size: Vector) : JLayeredPane() {
    init {
        bounds = Rectangle(0, 0, size.x, size.y)
    }

    override fun isPaintingOrigin(): Boolean = true
}

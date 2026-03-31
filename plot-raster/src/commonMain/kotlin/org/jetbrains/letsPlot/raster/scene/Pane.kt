/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene


internal class Pane : Container() {
    var width: Float by variableAttr(0f)
    var height: Float by variableAttr(0f)

    companion object {
        val CLASS = ATTRIBUTE_REGISTRY.addClass(Pane::class)

        val WidthAttrSpec = CLASS.registerVariableAttr(Pane::width)
        val HeightAttrSpec = CLASS.registerVariableAttr(Pane::height)
    }
}

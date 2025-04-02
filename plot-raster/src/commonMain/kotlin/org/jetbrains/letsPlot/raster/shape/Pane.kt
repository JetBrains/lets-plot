/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape


internal class Pane : Container() {
    var width: Float by visualProp(0.0f)
    var height: Float by visualProp(0.0f)
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.composite

import org.jetbrains.letsPlot.commons.geometry.Rectangle

interface HasBounds {
    val bounds: Rectangle
}
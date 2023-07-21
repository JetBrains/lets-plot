/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.input

import org.jetbrains.letsPlot.commons.geometry.Vector

class InputMouseEvent(val location: Vector) {
    var isStopped = false
        private set

    fun stopPropagation() {
        isStopped = true
    }
}
/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.canvas

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class StateTest {
    @Test
    fun simple() {
        val context = ContextStateDelegate()
        context.save()
        context.beginPath()
        context.moveTo(0.0, 0.0)
        context.lineTo(1.0, 1.0)
        context.closePath()
        context.clip()

        assertThat(context.state.getCurrentState().clipPath)
            .usingRecursiveComparison()
            .isEqualTo(
            Path2d()
                .moveTo(0.0, 0.0)
                .lineTo(1.0, 1.0)
                .closePath()
        )

        context.restore()

        assertThat(context.state.getCurrentState().clipPath)
            .usingRecursiveComparison()
            .isEqualTo(null)
    }
}
/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.tooltip.text.EqSpecification
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.Eq.FORMAT
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.Eq.LHS
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.Eq.RHS
import org.jetbrains.letsPlot.core.spec.Option.SmoothOptions.Eq.THRESHOLD


class EqSpecConfig(
    opts: Map<String, Any>
): OptionsAccessor(opts) {

    fun create(): EqSpecification {
        return EqSpecification(
            lhs = getString(LHS),
            rhs = getString(RHS),
            formats = getStringList(FORMAT),
            threshold = getDouble(THRESHOLD),
        )
    }
}
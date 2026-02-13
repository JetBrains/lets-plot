/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.tooltip.text.EqSpecification
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.Eq.FORMAT
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.Eq.LHS
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.Eq.RHS
import org.jetbrains.letsPlot.core.spec.Option.LinesSpec.Eq.THRESHOLD


class EqSpecConfigParser(
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
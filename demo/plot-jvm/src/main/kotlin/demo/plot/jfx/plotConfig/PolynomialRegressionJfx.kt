/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.common.util.demoUtils.jfx.PlotSpecsDemoWindowJfx
import demo.plot.common.model.plotConfig.PolynomialRegression

fun main() {
    with(PolynomialRegression()) {
        PlotSpecsDemoWindowJfx(
            "Polynomial regression",
            plotSpecList()
        ).open()
    }
}

/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.jfx.plotConfig

import demo.plot.common.model.plotConfig.PolynomialRegression
import demo.common.jfx.demoUtils.PlotSpecsDemoWindowJfx

fun main() {
    with(PolynomialRegression()) {
        PlotSpecsDemoWindowJfx(
            "Polynomial regression",
            plotSpecList()
        ).open()
    }
}

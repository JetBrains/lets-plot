/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

/**
 * Due to not working `debug` when in test runner
 */
fun main() {
    val test = EdgeCasesTest()
    test.checkWithNaNInXYSeries("bin2d")
}

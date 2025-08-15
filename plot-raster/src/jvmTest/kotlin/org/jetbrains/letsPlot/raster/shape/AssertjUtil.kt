/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.assertj.core.api.ObjectAssert
import org.assertj.core.api.RecursiveComparisonAssert
import org.assertj.core.util.DoubleComparator
import org.jetbrains.letsPlot.core.canvas.Path2d

fun <T : ObjectAssert<Path2d>> T.hasCommands(
    vararg expected: Path2d.PathCommand,
    epsilon: Double = 1.0,
): RecursiveComparisonAssert<*> {
    return this
        .extracting(Path2d::getCommands)
        .usingRecursiveComparison()
        .withStrictTypeChecking()
        .withComparatorForType(DoubleComparator(epsilon), Double::class.javaObjectType)
        .isEqualTo(expected.toMutableList())
}

/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale.breaks

internal val DUMMY_FORMATTER: (Any) -> String =
    { v -> throw IllegalStateException("Unintendent use of dummy formatter for $v.") }
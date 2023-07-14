/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import org.jetbrains.letsPlot.core.plot.base.Aesthetics

interface PosProviderContext {
    val aesthetics: Aesthetics

    val groupCount: Int
}

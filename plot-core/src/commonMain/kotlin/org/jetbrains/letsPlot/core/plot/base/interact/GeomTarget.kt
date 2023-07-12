/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.interact

import org.jetbrains.letsPlot.core.plot.base.Aes

// `open` - for Mockito tests
open class GeomTarget(
    val hitIndex: Int,
    open val tipLayoutHint: TipLayoutHint,
    open val aesTipLayoutHints: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, TipLayoutHint>)

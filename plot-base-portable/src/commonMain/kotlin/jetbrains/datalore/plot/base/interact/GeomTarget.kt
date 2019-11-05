/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.interact

import jetbrains.datalore.plot.base.Aes

// `open` - for Mockito tests
open class GeomTarget(
    val hitIndex: Int,
    open val tipLayoutHint: TipLayoutHint,
    open val aesTipLayoutHints: Map<Aes<*>, TipLayoutHint>)

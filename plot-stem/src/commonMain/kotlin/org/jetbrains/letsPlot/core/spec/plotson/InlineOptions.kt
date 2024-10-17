/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

abstract class InlineOptions(
    toSpecDelegate: (Options) -> Any? = Options::properties
) : Options(toSpecDelegate = toSpecDelegate)
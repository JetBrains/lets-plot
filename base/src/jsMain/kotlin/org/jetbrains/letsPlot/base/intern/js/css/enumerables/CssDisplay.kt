/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.js.css.enumerables

enum class CssDisplay constructor(override val stringQualifier: String) : CssBaseValue {
    DEFAULT("default"),
    NONE("none"),
    BLOCK("block"),
    FLEX("flex"),
    GRID("grid"),
    INLINE_BLOCK("inline-block");
}

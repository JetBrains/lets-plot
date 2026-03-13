/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables

enum class CssAlignItem constructor(override val stringQualifier: String) : CssBaseValue {
    DEFAULT("default"),
    CENTER("center"),
    STRETCH("stretch"),
    FLEX_START("flex-start"),
    FLEX_END("flex-end");
}

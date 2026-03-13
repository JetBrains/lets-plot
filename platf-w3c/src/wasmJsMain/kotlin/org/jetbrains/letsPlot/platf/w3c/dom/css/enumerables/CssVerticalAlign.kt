/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables

enum class CssVerticalAlign constructor(override val stringQualifier: String) : CssBaseValue {
    BASELINE("baseline"),
    SUB("sub"),
    SUPER("super"),
    TOP("top"),
    TEXT_TOP("text_top"),
    MIDDLE("middle"),
    BOTTOM("bottom"),
    TEXT_BOTTOM("text_bottom");
}

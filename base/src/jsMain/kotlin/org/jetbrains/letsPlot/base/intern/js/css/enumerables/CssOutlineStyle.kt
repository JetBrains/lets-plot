/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.js.css.enumerables

enum class CssOutlineStyle constructor(override val stringQualifier: String) : CssBaseValue {
    NONE("none"),
    DASHED("dashed"),
    DOTTED("dotted"),
    DOUBLE("double"),
    GROOVE("groove"),
    INSET("inset"),
    OUTSET("outset"),
    RIDGE("ridge"),
    SOLID("solid");
}

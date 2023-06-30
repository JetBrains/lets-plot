/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.svg.w3c.domExtensions

import org.w3c.dom.css.CSSStyleDeclaration

fun CSSStyleDeclaration.clearProperty(name: String): CSSStyleDeclaration {
    removeProperty(name)
    return this
}

fun CSSStyleDeclaration.clearDisplay(): CSSStyleDeclaration = clearProperty("display")
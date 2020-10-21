/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.css.enumerables

enum class CssCursor constructor(override val stringQualifier: String) : CssBaseValue {
    DEFAULT("default"),
    POINTER("pointer");
}

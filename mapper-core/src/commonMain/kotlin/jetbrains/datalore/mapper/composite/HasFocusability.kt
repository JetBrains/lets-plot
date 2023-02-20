/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

import jetbrains.datalore.base.observable.property.Property

interface HasFocusability {
    fun focusable(): Property<Boolean>
}

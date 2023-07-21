/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.core.ecs

open class EcsRemovable {
    private var removeFlag = false

    fun setRemoveFlag() {
        removeFlag = true
    }

    fun hasRemoveFlag(): Boolean {
        return removeFlag
    }
}

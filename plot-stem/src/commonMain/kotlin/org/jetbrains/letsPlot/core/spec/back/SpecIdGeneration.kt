/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.commons.intern.concurrent.AtomicInteger

object SpecIdGeneration {
    var enabled: Boolean = true
        private set
    private val lastId = AtomicInteger(0)

    // For tests only
    fun disable() {
        enabled = false
    }

    // For tests only
    fun enable() {
        enabled = true
    }

    fun nextId(): String {
        check(enabled) { "SpecIdGeneration is disabled" }
        return "${lastId.incrementAndGet()}"
    }
}
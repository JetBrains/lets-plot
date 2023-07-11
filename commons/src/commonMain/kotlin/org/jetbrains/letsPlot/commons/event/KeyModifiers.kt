/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

class KeyModifiers(val isCtrl: Boolean, val isAlt: Boolean, val isShift: Boolean, val isMeta: Boolean) {

    private constructor() : this(false, false, false, false)

    companion object {

        private val EMPTY_MODIFIERS = KeyModifiers()

        fun emptyModifiers(): KeyModifiers {
            return EMPTY_MODIFIERS
        }

        fun withShift(): KeyModifiers {
            return KeyModifiers(false, false, true, false)
        }
    }

}

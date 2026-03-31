/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

import org.jetbrains.letsPlot.commons.event.MouseEvent
import org.jetbrains.letsPlot.core.interact.InteractionSpec.KeyModifier

abstract class ModifiersMatcher internal constructor() {
    abstract fun match(e: MouseEvent): Boolean

    companion object {
        private val MATCHER_BY_MODIFIER: Map<KeyModifier, (MouseEvent) -> Boolean> = mapOf(
            KeyModifier.ALT to { e -> e.modifiers.isAlt },
            KeyModifier.CTRL to { e -> e.modifiers.isCtrl },
            KeyModifier.META to { e -> e.modifiers.isMeta },
            KeyModifier.SHIFT to { e -> e.modifiers.isShift }
        )

        private val MATCHER_BY_MODIFIER_NOT: Map<KeyModifier, (MouseEvent) -> Boolean> = mapOf(
            KeyModifier.ALT to { e -> !e.modifiers.isAlt },
            KeyModifier.CTRL to { e -> !e.modifiers.isCtrl },
            KeyModifier.META to { e -> !e.modifiers.isMeta },
            KeyModifier.SHIFT to { e -> !e.modifiers.isShift }
        )

        val ANY_MODIFIERS = object : ModifiersMatcher() {
            override fun match(e: MouseEvent): Boolean {
                return true
            }
        }

        val NO_MODIFIERS = object : ModifiersMatcher() {
            override fun match(e: MouseEvent): Boolean {
                val modifiers = e.modifiers
                return !modifiers.isAlt && !modifiers.isCtrl && !modifiers.isMeta && !modifiers.isShift
            }
        }

        fun create(modifiers: List<KeyModifier>?): ModifiersMatcher {
            if (modifiers.isNullOrEmpty()) {
                return NO_MODIFIERS
            }

            val matchers = KeyModifier.entries.toTypedArray().map {
                if (it in modifiers) MATCHER_BY_MODIFIER.getValue(it)
                else MATCHER_BY_MODIFIER_NOT.getValue(it)
            }

            return object : ModifiersMatcher() {
                override fun match(e: MouseEvent): Boolean {
                    return matchers.all { it(e) }
                }
            }
        }
    }
}
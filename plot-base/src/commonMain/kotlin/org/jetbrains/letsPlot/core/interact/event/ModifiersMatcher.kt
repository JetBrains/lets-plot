/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact.event

import org.jetbrains.letsPlot.commons.event.MouseEvent

abstract class ModifiersMatcher internal constructor() {
    abstract fun match(e: MouseEvent): Boolean

    companion object {
        private val MATCHER_BY_MODIFIER: Map<String, (MouseEvent) -> Boolean> = mapOf(
            ToolInteractionSpec.KeyModifier.ALT to { e -> e.modifiers.isAlt },
            ToolInteractionSpec.KeyModifier.CTRL to { e -> e.modifiers.isCtrl },
            ToolInteractionSpec.KeyModifier.META to { e -> e.modifiers.isMeta },
            ToolInteractionSpec.KeyModifier.SHIFT to { e -> e.modifiers.isShift }
        )
        private val MATCHER_NOT_BY_MODIFIER: Map<String, (MouseEvent) -> Boolean> = mapOf(
            ToolInteractionSpec.KeyModifier.ALT to { e -> !e.modifiers.isAlt },
            ToolInteractionSpec.KeyModifier.CTRL to { e -> !e.modifiers.isCtrl },
            ToolInteractionSpec.KeyModifier.META to { e -> !e.modifiers.isMeta },
            ToolInteractionSpec.KeyModifier.SHIFT to { e -> !e.modifiers.isShift }
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

        fun create(modifiers: List<String>): ModifiersMatcher {
            if (modifiers.isEmpty()) {
                return NO_MODIFIERS
            }

            val allModifiers = setOf(
                ToolInteractionSpec.KeyModifier.ALT,
                ToolInteractionSpec.KeyModifier.CTRL,
                ToolInteractionSpec.KeyModifier.META,
                ToolInteractionSpec.KeyModifier.SHIFT
            )

            modifiers.forEach {
                check(it in allModifiers) {
                    "Unknown modifier: $it. Expected one of: $allModifiers"
                }
            }

            val matchers = modifiers.map { MATCHER_BY_MODIFIER.getValue(it) } +
                    @Suppress("ConvertArgumentToSet")
                    (allModifiers - modifiers).map { MATCHER_NOT_BY_MODIFIER.getValue(it) }


            return object : ModifiersMatcher() {

                override fun match(e: MouseEvent): Boolean {
                    return matchers.all { it(e) }
                }
            }
        }
    }
}
/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.interact

class InteractionSpec(
    val name: Name,
    val zoomBoxMode: ZoomBoxMode? = null,
    val keyModifiers: List<KeyModifier>? = null
) {
    enum class Name(val value: String) {
        WHEEL_ZOOM("wheel-zoom"),
        BOX_ZOOM("box-zoom"),
        DRAG_PAN("drag-pan"),
        ROLLBACK_ALL_CHANGES("rollback-all-changes");

        companion object {
            const val PROPERTY_NAME = "name"

            fun fromString(value: String): Name? = entries.find { it.value == value }
        }
    }

    enum class ZoomBoxMode(val value: String) {
        CORNER_START("corner-start"),
        CENTER_START("center-start");

        companion object {
            const val PROPERTY_NAME = "zoom-box-mode"

            fun fromString(value: String): ZoomBoxMode? = entries.find { it.value == value }
        }
    }

    enum class KeyModifier(val value: String) {
        CTRL("ctrl"),
        ALT("alt"),
        SHIFT("shift"),
        META("meta");

        companion object {
            const val PROPERTY_NAME = "key-modifiers"

            fun fromString(value: String): KeyModifier? = KeyModifier.entries.find { it.value == value.lowercase() }
        }
    }

    fun toMap(): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>(
            Name.PROPERTY_NAME to name.value
        )
        if (zoomBoxMode != null) {
            map[ZoomBoxMode.PROPERTY_NAME] = zoomBoxMode.value
        }
        if (!keyModifiers.isNullOrEmpty()) {
            map[KeyModifier.PROPERTY_NAME] = keyModifiers.map { it.value }
        }
        return map
    }

    companion object {
        fun fromMap(spec: Map<*, *>): InteractionSpec {
            @Suppress("UNCHECKED_CAST")
            spec as Map<Any?, Any?>

            val nameValue = spec.getValue(Name.PROPERTY_NAME).toString()
            val name = Name.fromString(nameValue)
                ?: throw IllegalArgumentException("Unknown interaction name: $nameValue. Use: ${Name.entries.joinToString { it.value }}")

            val zoomBoxMode = if (name == Name.BOX_ZOOM) {
                val modeValue = spec[ZoomBoxMode.PROPERTY_NAME] as? String
                if (modeValue != null) {
                    ZoomBoxMode.fromString(modeValue)
                        ?: throw IllegalArgumentException("Unknown zoom box mode: $modeValue. Use: ${ZoomBoxMode.entries.joinToString { it.value }}")
                } else {
                    null
                }
            } else {
                null
            }

            val keyModifiers = (spec[KeyModifier.PROPERTY_NAME] as? List<*>)?.map { modifierStr ->
                @Suppress("NAME_SHADOWING")
                val modifierStr = modifierStr as? String
                    ?: throw IllegalArgumentException("Key modifier must be a string: $modifierStr")
                KeyModifier.fromString(modifierStr)
                    ?: throw IllegalArgumentException("Unknown key modifier: $modifierStr. Use: ${KeyModifier.entries.joinToString { it.value }}")
            }

            return InteractionSpec(name, zoomBoxMode, keyModifiers)
        }
    }
}
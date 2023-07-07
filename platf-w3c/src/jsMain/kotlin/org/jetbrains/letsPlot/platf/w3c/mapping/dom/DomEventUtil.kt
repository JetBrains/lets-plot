/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.platf.dom

import jetbrains.datalore.base.event.*
import org.jetbrains.letsPlot.platf.w3c.dom.events.DomMouseButtons
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent

object DomEventUtil {
    private fun toKeyEvent(e: KeyboardEvent): KeyEvent {
        val key = DomKeyCodeMapper.getKey(e.keyCode)

        val modifiers = HashSet<ModifierKey>()
        if (e.ctrlKey) {
            modifiers.add(ModifierKey.CONTROL)
        }
        if (e.altKey) {
            modifiers.add(ModifierKey.ALT)
        }
        if (e.shiftKey) {
            modifiers.add(ModifierKey.SHIFT)
        }
        if (e.metaKey) {
            modifiers.add(ModifierKey.META)
        }
        return KeyEvent(key, e.charCode.toChar(), modifiers)
    }

    fun matches(e: KeyboardEvent, keyStrokeSpec: KeyStrokeSpec): Boolean {
        return keyStrokeSpec.matches(toKeyEvent(e).keyStroke)
    }

    fun dispatchKeyPress(e: KeyboardEvent, handler: (KeyEvent) -> Unit): Boolean {
        val event = toKeyEvent(e)
        handler(event)

        //disable back button
        if (event.key === Key.BACKSPACE) return false

        //disable navigation keys to prevent browser scrolling
        if (event.key === Key.UP || event.key === Key.DOWN || event.key === Key.LEFT || event.key === Key.RIGHT) return false

        //disable space scrolling in case of unhandled space
        if (event.key === Key.SPACE) return false

        //disable shift+arrow selection
        if (event.`is`(KeyStrokeSpecs.SELECT_LEFT) || event.`is`(KeyStrokeSpecs.SELECT_UP)
            || event.`is`(KeyStrokeSpecs.SELECT_RIGHT) || event.`is`(KeyStrokeSpecs.SELECT_DOWN)
        ) {
            return false
        }

        //disable back forward with Ctrl/Cmd + [ / ]
        if (event.`is`(KeyStrokeSpecs.MATCHING_CONSTRUCTS)) return false

        //disable tab navigation
        return if (event.`is`(Key.TAB) || event.`is`(Key.TAB, ModifierKey.SHIFT)) false else !event.isConsumed

    }

    internal fun dispatchKeyRelease(e: KeyboardEvent, handler: (KeyEvent) -> Unit): Boolean {
        val event = toKeyEvent(e)
        handler(event)
        return !event.isConsumed
    }

    internal fun dispatchKeyType(e: KeyboardEvent, handler: (KeyEvent) -> Unit): Boolean {
        val event = toKeyEvent(e)
        if (e.charCode < 0x20 || e.charCode == 0x7F) return true
        if (e.charCode.toChar() == '\n') return true
        if (e.charCode.toChar() == '\r') return true
        if (event.has(ModifierKey.META) || event.has(ModifierKey.CONTROL) && !event.has(ModifierKey.ALT)) return true

        handler(event)

        return !event.isConsumed
    }

    fun getButton(e: MouseEvent): Button {
        return when (e.button.toInt()) {
            DomMouseButtons.BUTTON_LEFT -> Button.LEFT
            DomMouseButtons.BUTTON_MIDDLE -> Button.MIDDLE
            DomMouseButtons.BUTTON_RIGHT -> Button.RIGHT
            else -> Button.NONE
        }
    }

    fun getModifiers(e: MouseEvent): KeyModifiers {
        val ctrlKey = e.ctrlKey
        val altKey = e.altKey
        val shiftKey = e.shiftKey
        val metaKey = e.metaKey
        return KeyModifiers(ctrlKey, altKey, shiftKey, metaKey)
    }

}
/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

class KeyEvent : Event {

    val keyStroke: KeyStroke
    val keyChar: Char

    val key: Key
        get() = keyStroke.key

    val modifiers: Set<ModifierKey>
        get() = keyStroke.modifiers

    constructor(keyStroke: KeyStroke) {
        this.keyStroke = keyStroke
        keyChar = 0.toChar()
    }

    constructor(key: Key, ch: Char = 0.toChar()) {
        keyStroke = KeyStroke(key, emptyList())
        keyChar = ch
    }

    constructor(key: Key, ch: Char, modifiers: Collection<ModifierKey>) {
        keyStroke = KeyStroke(key, modifiers)
        keyChar = ch
    }

    fun `is`(key: Key, vararg modifiers: ModifierKey): Boolean {
        return keyStroke.`is`(key, *modifiers)
    }

    fun `is`(vararg specs: KeyStrokeSpec): Boolean {
        for (s in specs) {
            if (s.matches(keyStroke)) return true
        }
        return false
    }

    fun `is`(vararg specs: KeyStroke): Boolean {
        for (s in specs) {
            if (s.matches(keyStroke)) return true
        }
        return false
    }

    fun has(key: ModifierKey): Boolean {
        return keyStroke.has(key)
    }

    fun copy(): KeyEvent {
        return KeyEvent(key, keyChar, modifiers)
    }

    override fun toString(): String {
        return keyStroke.toString()
    }
}

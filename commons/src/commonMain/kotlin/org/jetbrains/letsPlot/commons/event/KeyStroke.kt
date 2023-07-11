/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

class KeyStroke {
    val key: Key
    val modifiers: Set<ModifierKey>

    constructor(key: Key, vararg modifiers: ModifierKey) : this(key, modifiers.asList())

    constructor(key: Key, modifiers: Collection<ModifierKey>) {
        this.key = key
        this.modifiers = HashSet(modifiers)
    }

    fun has(key: ModifierKey): Boolean {
        return modifiers.contains(key)
    }

    fun `is`(key: Key, vararg modifiers: ModifierKey): Boolean {
        return matches(KeyStroke(key, *modifiers))
    }

    fun matches(keyStroke: KeyStroke): Boolean {
        return equals(keyStroke)
    }

    fun with(key: ModifierKey): KeyStroke {
        val keys = HashSet(modifiers)
        keys.add(key)
        return KeyStroke(this.key, keys)
    }

    override fun hashCode(): Int {
        return key.hashCode() * 31 + modifiers.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is KeyStroke) return false
        val otherKeyStroke = other as KeyStroke?

        return key === otherKeyStroke!!.key && modifiers == otherKeyStroke!!.modifiers
    }

    override fun toString(): String {
        return "$key $modifiers"
    }
}
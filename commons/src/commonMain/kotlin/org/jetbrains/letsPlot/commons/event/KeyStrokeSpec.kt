/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

class KeyStrokeSpec {

    private val myKeyStrokes: Array<KeyStroke>

    val keyStrokes: Iterable<KeyStroke>
        get() = listOf(*myKeyStrokes)

    val isEmpty: Boolean
        get() = myKeyStrokes.isEmpty()

    constructor(key: Key, vararg modifiers: ModifierKey) {
        myKeyStrokes = arrayOf(KeyStroke(key, *modifiers))
    }

    constructor(keyStrokes: Collection<KeyStroke>) {
        myKeyStrokes = keyStrokes.toTypedArray()
    }

    constructor(vararg keyStrokes: KeyStroke) {
        myKeyStrokes = arrayOf(*keyStrokes)
    }

    fun matches(keyStroke: KeyStroke): Boolean {
        for (spec in myKeyStrokes) {
            if (spec.matches(keyStroke)) {
                return true
            }
        }
        return false
    }

    fun with(key: ModifierKey): KeyStrokeSpec {
        val modified = ArrayList<KeyStroke>()
        for (keyStroke in myKeyStrokes) {
            modified.add(keyStroke.with(key))
        }
        return KeyStrokeSpec(modified)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        val that = other as KeyStrokeSpec?
        return keyStrokes == that!!.keyStrokes
    }

    override fun hashCode(): Int {
        return keyStrokes.hashCode()
    }

    override fun toString(): String {
        return keyStrokes.toString()
    }

}
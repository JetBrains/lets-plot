/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.event

import org.jetbrains.letsPlot.commons.event.ModifierKey.*

object KeyStrokeSpecs {
    val COPY = composite(ctrlOrMeta(Key.C), KeyStrokeSpec(Key.INSERT, CONTROL))
    val CUT = composite(ctrlOrMeta(Key.X), KeyStrokeSpec(Key.DELETE, SHIFT))
    val PASTE = composite(ctrlOrMeta(Key.V), KeyStrokeSpec(Key.INSERT, SHIFT))

    val UNDO = ctrlOrMeta(Key.Z)
    val REDO = UNDO.with(SHIFT)

    val COMPLETE = KeyStrokeSpec(Key.SPACE, CONTROL)

    val SHOW_DOC = composite(KeyStrokeSpec(Key.F1), ctrlOrMeta(Key.J))

    val HELP = composite(ctrlOrMeta(Key.I), ctrlOrMeta(Key.F1))

    val HOME = composite(KeyStroke(Key.HOME), KeyStroke(Key.LEFT, META))
    val END = composite(KeyStroke(Key.END), KeyStroke(Key.RIGHT, META))

    val FILE_HOME = ctrlOrMeta(Key.HOME)
    val FILE_END = ctrlOrMeta(Key.END)

    val PREV_WORD = ctrlOrAlt(Key.LEFT)
    val NEXT_WORD = ctrlOrAlt(Key.RIGHT)

    val NEXT_EDITABLE = ctrlOrMeta(Key.RIGHT, ALT)
    val PREV_EDITABLE = ctrlOrMeta(Key.LEFT, ALT)

    val SELECT_ALL = ctrlOrMeta(Key.A)

    val SELECT_FILE_HOME = FILE_HOME.with(SHIFT)
    val SELECT_FILE_END = FILE_END.with(SHIFT)

    val SELECT_HOME = HOME.with(SHIFT)
    val SELECT_END = END.with(SHIFT)

    val SELECT_WORD_FORWARD = NEXT_WORD.with(SHIFT)
    val SELECT_WORD_BACKWARD = PREV_WORD.with(SHIFT)

    val SELECT_LEFT = KeyStrokeSpec(Key.LEFT, SHIFT)
    val SELECT_RIGHT = KeyStrokeSpec(Key.RIGHT, SHIFT)

    val SELECT_UP = KeyStrokeSpec(Key.UP, SHIFT)
    val SELECT_DOWN = KeyStrokeSpec(Key.DOWN, SHIFT)

    val INCREASE_SELECTION = KeyStrokeSpec(Key.UP, ALT)
    val DECREASE_SELECTION = KeyStrokeSpec(Key.DOWN, ALT)

    val INSERT_BEFORE = composite(
            KeyStroke(Key.ENTER, add(META)),
            KeyStroke(Key.INSERT),
            KeyStroke(Key.ENTER, add(CONTROL))
    )
    val INSERT_AFTER = KeyStrokeSpec(Key.ENTER)
    val INSERT = composite(INSERT_BEFORE, INSERT_AFTER)

    val DUPLICATE = ctrlOrMeta(Key.D)

    val DELETE_CURRENT = composite(ctrlOrMeta(Key.BACKSPACE), ctrlOrMeta(Key.DELETE))

    val DELETE_TO_WORD_START = KeyStrokeSpec(Key.BACKSPACE, ALT)

    val MATCHING_CONSTRUCTS = composite(ctrlOrMeta(Key.LEFT_BRACE, ALT), ctrlOrMeta(Key.RIGHT_BRACE, ALT))

    val NAVIGATE = ctrlOrMeta(Key.B)
    val NAVIGATE_BACK = ctrlOrMeta(Key.LEFT_BRACE)
    val NAVIGATE_FORWARD = ctrlOrMeta(Key.RIGHT_BRACE)

    fun ctrlOrMeta(key: Key, vararg modifiers: ModifierKey): KeyStrokeSpec {
        return composite(KeyStroke(key, add(CONTROL, *modifiers)), KeyStroke(key, add(META, *modifiers)))
    }

    fun ctrlOrAlt(key: Key, vararg modifiers: ModifierKey): KeyStrokeSpec {
        return composite(KeyStroke(key, add(CONTROL, *modifiers)), KeyStroke(key, add(ALT, *modifiers)))
    }

    private fun add(key: ModifierKey, vararg otherKeys: ModifierKey): Set<ModifierKey> {
        val result = HashSet(otherKeys.asList())
        result.add(key)
        return result
    }

    fun composite(vararg specs: KeyStrokeSpec): KeyStrokeSpec {
        val keyStrokes = HashSet<KeyStroke>()
        for (spec in specs) {
            for (ks in spec.keyStrokes) {
                keyStrokes.add(ks)
            }
        }
        return KeyStrokeSpec(keyStrokes)
    }

    fun composite(vararg specs: KeyStroke): KeyStrokeSpec {
        return KeyStrokeSpec(*specs)
    }

    fun withoutShift(spec: KeyStrokeSpec): KeyEvent {
        val keyStroke = spec.keyStrokes.iterator().next()
        val modifiers = keyStroke.modifiers
        val withoutShift = HashSet<ModifierKey>()
        for (modifier in modifiers) {
            if (modifier !== SHIFT) {
                withoutShift.add(modifier)
            }
        }
        return KeyEvent(keyStroke.key, 0.toChar(), withoutShift)
    }
}

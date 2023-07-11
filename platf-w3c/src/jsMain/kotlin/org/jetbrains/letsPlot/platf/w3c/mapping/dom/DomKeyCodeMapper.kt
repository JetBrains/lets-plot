/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.platf.dom

import org.jetbrains.letsPlot.commons.event.Key

internal object DomKeyCodeMapper {

    private val KEY_ARRAY = arrayOfNulls<Key>(200)

    fun getKey(code: Int): Key {
        if (code < KEY_ARRAY.size) {
            val result = KEY_ARRAY[code]
            if (result != null) {
                return result
            }
        }
        return Key.UNKNOWN
    }

    init {
        KEY_ARRAY[KeyCodes.KEY_ALT] = Key.ALT
        KEY_ARRAY[KeyCodes.KEY_BACKSPACE] = Key.BACKSPACE
        KEY_ARRAY[KeyCodes.KEY_CTRL] = Key.CONTROL
        KEY_ARRAY[KeyCodes.KEY_DELETE] = Key.DELETE
        KEY_ARRAY[KeyCodes.KEY_DOWN] = Key.DOWN
        KEY_ARRAY[KeyCodes.KEY_END] = Key.END
        KEY_ARRAY[KeyCodes.KEY_ENTER] = Key.ENTER
        KEY_ARRAY[KeyCodes.KEY_ESCAPE] = Key.ESCAPE
        KEY_ARRAY[KeyCodes.KEY_HOME] = Key.HOME
        KEY_ARRAY[KeyCodes.KEY_LEFT] = Key.LEFT
        KEY_ARRAY[KeyCodes.KEY_PAGEDOWN] = Key.PAGE_DOWN
        KEY_ARRAY[KeyCodes.KEY_PAGEUP] = Key.PAGE_UP
        KEY_ARRAY[KeyCodes.KEY_RIGHT] = Key.RIGHT
        KEY_ARRAY[KeyCodes.KEY_SHIFT] = Key.SHIFT
        KEY_ARRAY[KeyCodes.KEY_TAB] = Key.TAB
        KEY_ARRAY[KeyCodes.KEY_UP] = Key.UP
        KEY_ARRAY[45] = Key.INSERT
        KEY_ARRAY[32] = Key.SPACE

        KEY_ARRAY['A'.code] = Key.A
        KEY_ARRAY['B'.code] = Key.B
        KEY_ARRAY['C'.code] = Key.C
        KEY_ARRAY['D'.code] = Key.D
        KEY_ARRAY['E'.code] = Key.E
        KEY_ARRAY['F'.code] = Key.F
        KEY_ARRAY['G'.code] = Key.G
        KEY_ARRAY['H'.code] = Key.H
        KEY_ARRAY['I'.code] = Key.I
        KEY_ARRAY['J'.code] = Key.J
        KEY_ARRAY['K'.code] = Key.K
        KEY_ARRAY['L'.code] = Key.L
        KEY_ARRAY['M'.code] = Key.M
        KEY_ARRAY['N'.code] = Key.N
        KEY_ARRAY['O'.code] = Key.O
        KEY_ARRAY['P'.code] = Key.P
        KEY_ARRAY['Q'.code] = Key.Q
        KEY_ARRAY['R'.code] = Key.R
        KEY_ARRAY['S'.code] = Key.S
        KEY_ARRAY['T'.code] = Key.T
        KEY_ARRAY['U'.code] = Key.U
        KEY_ARRAY['V'.code] = Key.V
        KEY_ARRAY['W'.code] = Key.W
        KEY_ARRAY['X'.code] = Key.X
        KEY_ARRAY['Y'.code] = Key.Y
        KEY_ARRAY['Z'.code] = Key.Z

        KEY_ARRAY[219] = Key.LEFT_BRACE
        KEY_ARRAY[221] = Key.RIGHT_BRACE

        KEY_ARRAY['0'.code] = Key.DIGIT_0
        KEY_ARRAY['1'.code] = Key.DIGIT_1
        KEY_ARRAY['2'.code] = Key.DIGIT_2
        KEY_ARRAY['3'.code] = Key.DIGIT_3
        KEY_ARRAY['4'.code] = Key.DIGIT_4
        KEY_ARRAY['5'.code] = Key.DIGIT_5
        KEY_ARRAY['6'.code] = Key.DIGIT_6
        KEY_ARRAY['7'.code] = Key.DIGIT_7
        KEY_ARRAY['8'.code] = Key.DIGIT_8
        KEY_ARRAY['9'.code] = Key.DIGIT_9

        KEY_ARRAY[112] = Key.F1
        KEY_ARRAY[113] = Key.F2
        KEY_ARRAY[114] = Key.F3
        KEY_ARRAY[115] = Key.F4
        KEY_ARRAY[116] = Key.F5
        KEY_ARRAY[117] = Key.F6
        KEY_ARRAY[118] = Key.F7
        KEY_ARRAY[119] = Key.F8
        KEY_ARRAY[120] = Key.F9
        KEY_ARRAY[121] = Key.F10
        KEY_ARRAY[122] = Key.F11
        KEY_ARRAY[123] = Key.F12

        KEY_ARRAY[188] = Key.COMMA
        KEY_ARRAY[190] = Key.PERIOD
        KEY_ARRAY[191] = Key.SLASH
        KEY_ARRAY[192] = Key.BACK_QUOTE
    }
}
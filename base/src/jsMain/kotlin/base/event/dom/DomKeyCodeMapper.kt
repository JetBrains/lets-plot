package jetbrains.datalore.base.event.dom

import jetbrains.datalore.base.domCore.event.KeyCodes
import jetbrains.datalore.base.event.Key

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

        KEY_ARRAY['A'.toInt()] = Key.A
        KEY_ARRAY['B'.toInt()] = Key.B
        KEY_ARRAY['C'.toInt()] = Key.C
        KEY_ARRAY['D'.toInt()] = Key.D
        KEY_ARRAY['E'.toInt()] = Key.E
        KEY_ARRAY['F'.toInt()] = Key.F
        KEY_ARRAY['G'.toInt()] = Key.G
        KEY_ARRAY['H'.toInt()] = Key.H
        KEY_ARRAY['I'.toInt()] = Key.I
        KEY_ARRAY['J'.toInt()] = Key.J
        KEY_ARRAY['K'.toInt()] = Key.K
        KEY_ARRAY['L'.toInt()] = Key.L
        KEY_ARRAY['M'.toInt()] = Key.M
        KEY_ARRAY['N'.toInt()] = Key.N
        KEY_ARRAY['O'.toInt()] = Key.O
        KEY_ARRAY['P'.toInt()] = Key.P
        KEY_ARRAY['Q'.toInt()] = Key.Q
        KEY_ARRAY['R'.toInt()] = Key.R
        KEY_ARRAY['S'.toInt()] = Key.S
        KEY_ARRAY['T'.toInt()] = Key.T
        KEY_ARRAY['U'.toInt()] = Key.U
        KEY_ARRAY['V'.toInt()] = Key.V
        KEY_ARRAY['W'.toInt()] = Key.W
        KEY_ARRAY['X'.toInt()] = Key.X
        KEY_ARRAY['Y'.toInt()] = Key.Y
        KEY_ARRAY['Z'.toInt()] = Key.Z

        KEY_ARRAY[219] = Key.LEFT_BRACE
        KEY_ARRAY[221] = Key.RIGHT_BRACE

        KEY_ARRAY['0'.toInt()] = Key.DIGIT_0
        KEY_ARRAY['1'.toInt()] = Key.DIGIT_1
        KEY_ARRAY['2'.toInt()] = Key.DIGIT_2
        KEY_ARRAY['3'.toInt()] = Key.DIGIT_3
        KEY_ARRAY['4'.toInt()] = Key.DIGIT_4
        KEY_ARRAY['5'.toInt()] = Key.DIGIT_5
        KEY_ARRAY['6'.toInt()] = Key.DIGIT_6
        KEY_ARRAY['7'.toInt()] = Key.DIGIT_7
        KEY_ARRAY['8'.toInt()] = Key.DIGIT_8
        KEY_ARRAY['9'.toInt()] = Key.DIGIT_9

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

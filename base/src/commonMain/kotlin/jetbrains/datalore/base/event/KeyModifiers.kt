package jetbrains.datalore.base.event

class KeyModifiers(val isCtrl: Boolean, val isAlt: Boolean, val isShift: Boolean, val isMeta: Boolean) {

    private constructor() : this(false, false, false, false)

    companion object {

        private val EMPTY_MODIFIERS = KeyModifiers()

        fun emptyModifiers(): KeyModifiers {
            return EMPTY_MODIFIERS
        }

        fun withShift(): KeyModifiers {
            return KeyModifiers(false, false, true, false)
        }
    }

}

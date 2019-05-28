package jetbrains.datalore.visualization.base.canvas

class CssFontParser private constructor(private val myMatchResult: MatchResult) {

    val fontFamily: String?
        get() = getString(FONT_FAMILY)

    val sizeString: String?
        get() = getString(SIZE_STRING)

    val fontSize: Double?
        get() = getDouble(FONT_SIZE)

    val lineHeight: Double?
        get() = getDouble(LINE_HEIGHT)

    private fun getString(index: Int): String {
        return myMatchResult.groupValues[index]
    }

    private fun getDouble(index: Int): Double? {
        val v = getString(index)
        return if (v.isEmpty()) null else v.toDouble()
    }

    companion object {
        private val FONT_SCALABLE_VALUES = Regex("((\\d+\\.?\\d*)px(?:/(\\d+\\.?\\d*)px)?) ?([a-zA-Z -]+)?")
        private const val SIZE_STRING = 1
        private const val FONT_SIZE = 2
        private const val LINE_HEIGHT = 3
        private const val FONT_FAMILY = 4

        fun create(font: String): CssFontParser? {
            val matchResult = FONT_SCALABLE_VALUES.find(font)
            return if (matchResult == null) null else CssFontParser(matchResult)
        }
    }
}

package jetbrains.datalore.visualization.base.canvas

//import com.google.gwt.regexp.shared.MatchResult
//import com.google.gwt.regexp.shared.RegExp
//
//import java.lang.Double.parseDouble

class CssFontParser private constructor(private val myMatchResult: MatchResult) {

    val fontFamily: String? = "System"
// ToDo!!!
//        get() = getString(FONT_FAMILY)

    val sizeString: String? = "10.0"
// ToDo!!!
//        get() = getString(SIZE_STRING)

    val fontSize: Double? = 10.0
// ToDo!!!
//        get() = getDouble(FONT_SIZE)

    val lineHeight: Double? = 10.0
// ToDo!!!
//        get() = getDouble(LINE_HEIGHT)

// ToDo!!!
//    private fun getString(index: Int): String? {
//        return myMatchResult.getGroup(index)
//    }

// ToDo!!!
//    private fun getDouble(index: Int): Double? {
//        val v = getString(index)
//        return if (v == null) null else v.toDouble()
//    }

    companion object {
        // ToDo!!!
        //        private val FONT_SCALABLE_VALUES = RegExp.compile("((\\d+\\.?\\d*)px(?:/(\\d+\\.?\\d*)px)?) ?([a-zA-Z -]+)?")
        private val SIZE_STRING = 1
        private val FONT_SIZE = 2
        private val LINE_HEIGHT = 3
        private val FONT_FAMILY = 4

        fun create(font: String): CssFontParser? {
// ToDo!!!
//            val matchResult = FONT_SCALABLE_VALUES.exec(font)
//            return if (matchResult == null) null else CssFontParser(matchResult)
            return null
        }
    }
}

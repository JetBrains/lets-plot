package jetbrains.datalore.visualization.base.svg

class SvgPathData internal constructor(private val myPathData: String) {

    enum class Action private constructor(private val myChar: Char) {
        MOVE_TO('m'),
        LINE_TO('l'),
        HORIZONTAL_LINE_TO('h'),
        VERTICAL_LINE_TO('v'),
        CURVE_TO('c'),
        SMOOTH_CURVE_TO('s'),
        QUADRATIC_BEZIER_CURVE_TO('q'),
        SMOOTH_QUADRATIC_BEZIER_CURVE_TO('t'),
        ELLIPTICAL_ARC('a'),
        CLOSE_PATH('z');

        fun relativeCmd(): Char {
            return myChar
        }

        fun absoluteCmd(): Char {
            return myChar.toUpperCase()
        }

        companion object {

            private val MAP = HashMap<Char, Action>()

            init {
                for (v in values()) {
                    MAP[v.absoluteCmd()] = v
                    MAP[v.relativeCmd()] = v
                }
            }

            operator fun get(c: Char): Action {
                if (MAP.containsKey(c)) {
                    return MAP[c]!!
                }
                throw IllegalArgumentException("No enum constant " + Action::class + "@myChar." + c)
            }
        }
    }

    override fun toString(): String {
        return myPathData
    }

    companion object {
        val EMPTY = SvgPathData("")
    }
}
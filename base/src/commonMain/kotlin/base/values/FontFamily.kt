package jetbrains.datalore.base.values

class FontFamily private constructor(private val myName: String) {

    override fun toString(): String {
        return myName
    }

    companion object {
        val MONOSPACED = forName("monospace")
        val SERIF = forName("serif")

        fun forName(name: String): FontFamily {
            return FontFamily(name)
        }
    }
}
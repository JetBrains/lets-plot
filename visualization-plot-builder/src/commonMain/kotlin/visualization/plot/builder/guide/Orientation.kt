package jetbrains.datalore.visualization.plot.builder.guide

enum class Orientation(private val myValue: String) {
    LEFT("LEFT"),
    RIGHT("RIGHT"),
    TOP("TOP"),
    BOTTOM("BOTTOM");

    val isHorizontal: Boolean
        get() = this == TOP || this == BOTTOM

    override fun toString(): String {
        return "Orientation{" +
                "myValue='" + myValue + '\''.toString() +
                '}'.toString()
    }
}

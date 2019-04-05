package jetbrains.datalore.visualization.base.svg.slim

internal object AttributeUtil {

    private fun doubleOrDefault(e: SlimBase, attrIndex: Int, d: Double): Double {
        val o = e.getAttribute(attrIndex)
        return if (o == null) d else java.lang.Double.parseDouble(o!!.toString())
    }

    fun zeroIfNull(e: SlimBase, attrIndex: Int): Double {
        return doubleOrDefault(e, attrIndex, 0.0)
    }

    fun oneIfNull(e: SlimBase, attrIndex: Int): Double {
        return doubleOrDefault(e, attrIndex, 1.0)
    }

    fun stringOrNull(e: SlimBase, attrIndex: Int): String? {
        val o = e.getAttribute(attrIndex)
        return if (o == null) null else o!!.toString()
    }
}

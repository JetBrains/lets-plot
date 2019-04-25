package jetbrains.datalore.visualization.plot.gog.core.data

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.base.gcommon.base.Strings
import java.util.*

object Dummies {

    private val PREFIX = "__"

    fun isDummyVar(varName: String): Boolean {
        if (!Strings.isNullOrEmpty(varName) && varName.length > PREFIX.length && varName.startsWith(PREFIX)) {
            val numStr = varName.substring(PREFIX.length)
            return numStr.matches("[0-9]+".toRegex())
        }
        return false
    }

    fun dummyNames(count: Int): List<String> {
        val l = LinkedList<String>()
        for (i in 0 until count) {
            l.add(PREFIX + i)
        }
        return l
    }

    fun newDummy(varName: String): DataFrame.Variable {
        Preconditions.checkArgument(isDummyVar(varName), "Not a dummy var name")
        // no label
        return DataFrame.Variable(varName, DataFrame.Variable.Source.ORIGIN, "")
    }
}

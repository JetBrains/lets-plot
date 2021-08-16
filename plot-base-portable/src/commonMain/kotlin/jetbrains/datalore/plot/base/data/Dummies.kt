/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.data

import jetbrains.datalore.base.gcommon.base.Strings
import jetbrains.datalore.plot.base.DataFrame

object Dummies {

    private const val PREFIX = "__"

    fun isDummyVar(varName: String): Boolean {
        if (!Strings.isNullOrEmpty(varName) && varName.length > PREFIX.length && varName.startsWith(
                PREFIX
            )
        ) {
            val numStr = varName.substring(PREFIX.length)
            return numStr.matches("[0-9]+".toRegex())
        }
        return false
    }

    fun dummyNames(count: Int): List<String> {
        val l = ArrayList<String>()
        for (i in 0 until count) {
            l.add(PREFIX + i)
        }
        return l
    }

    fun newDummy(varName: String): DataFrame.Variable {
        require(isDummyVar(varName)) { "Not a dummy var name" }
        // no label
        return DataFrame.Variable(varName, DataFrame.Variable.Source.ORIGIN, "")
    }
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plotDemo.model.util

import org.jetbrains.letsPlot.core.plot.base.Aesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.builder.assemble.GeomContextBuilder

object DemoUtil {
    fun <T> interlace(l1: List<T>, l2: List<T>): List<T> {
        val l = ArrayList<T>()
        val i1 = l1.iterator()
        val i2 = l2.iterator()
        while (i1.hasNext() || i2.hasNext()) {
            if (i1.hasNext()) {
                l.add(i1.next())
            }
            if (i2.hasNext()) {
                l.add(i2.next())
            }
        }
        return l
    }

    fun geomContext(aes: Aesthetics): GeomContext {
        return GeomContextBuilder().aesthetics(aes).build()
    }
}

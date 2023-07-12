/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.sampling.method

import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable
import jetbrains.datalore.plot.builder.data.generateData
import kotlin.random.Random

internal abstract class GroupSamplingTestBase {

    protected var data: DataFrame? = null
        private set
    protected var groupMapper: ((Int) -> Int)? = null
        private set

    fun setUp(pointCount: Int, groupCount: Int) {
        val data = generateData(pointCount, listOf("x", "y", "c"))

        // add groups
        val random = Random(9999)
        val groups = (0 until pointCount).map { random.nextInt(groupCount) }
        check(groups.distinct().count() == groupCount)

        groupMapper = { groups[it] }
        this.data = data.builder()
                .put(GROUP_SERIE_VAR, groups.map { it.toString() })
                .build()
    }

    companion object {
        val GROUP_SERIE_VAR = Variable("group_serie")
    }
}

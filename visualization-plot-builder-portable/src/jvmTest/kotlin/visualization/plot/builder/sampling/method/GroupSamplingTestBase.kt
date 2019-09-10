package jetbrains.datalore.visualization.plot.builder.sampling.method

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.DataFrame.Variable
import jetbrains.datalore.visualization.plot.builder.data.generateData
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
        checkState(groups.distinct().count() == groupCount)

        groupMapper = { groups[it] }
        this.data = data.builder()
                .put(GROUP_SERIE_VAR, groups.map { it.toString() })
                .build()
    }

    companion object {
        val GROUP_SERIE_VAR = Variable("group_serie")
    }
}

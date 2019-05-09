package jetbrains.datalore.visualization.plot.gog.core.data.sampling

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import jetbrains.datalore.visualization.plot.gog.core.data.TestUtil
import java.util.*
import java.util.Arrays.asList
import java.util.stream.Collectors.toList
import java.util.stream.IntStream.range

internal abstract class GroupSamplingTestBase {

    protected var data: DataFrame? = null
        private set
    protected var groupMapper: ((Int) -> Int)? = null
        private set

    fun setUp(pointCount: Int, groupCount: Int) {
        val data = TestUtil.generateData(pointCount, asList("x", "y", "c"))

        // add groups
        val random = Random(9999)
        val groups = range(0, pointCount).mapToObj { random.nextInt(groupCount) }.collect(toList())
        checkState(groups.stream().distinct().count() == groupCount.toLong())

        groupMapper = { groups[it] }
        this.data = data.builder()
                .put(GROUP_SERIE_VAR, groups.stream().map { it.toString() }.collect(toList()))
                .build()
    }

    companion object {
        val GROUP_SERIE_VAR = Variable("group_serie")
    }
}

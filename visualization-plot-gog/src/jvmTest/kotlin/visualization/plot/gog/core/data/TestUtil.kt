package jetbrains.datalore.visualization.plot.gog.core.data

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Builder
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import java.util.stream.Collectors.toList
import java.util.stream.IntStream

object TestUtil {
    fun generateData(rowCount: Int, varNames: Collection<String>): DataFrame {
        val variables = varNames.stream()
                .map<Variable> { Variable(it) }
                .collect(toList())

        val builder = Builder()
        for (`var` in variables) {
            builder.put(`var`, toSerie(`var`.name, indices(rowCount)))
        }

        return builder.build()
    }

    internal fun indices(count: Int): List<Int> {
        return IntStream.range(0, count).boxed().collect(toList())
    }

    internal fun toSerie(prefix: String, ints: Collection<Int>): List<*> {
        return ints.stream()
                .map { v -> prefix + v!! }
                .collect(toList())
    }
}

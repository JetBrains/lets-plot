package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.builder.assemble.PlotFacets
import jetbrains.datalore.visualization.plot.gog.config.Option.Facet.NAME
import jetbrains.datalore.visualization.plot.gog.config.Option.Facet.X
import jetbrains.datalore.visualization.plot.gog.config.Option.Facet.Y

internal class FacetConfig(options: Map<*, *>) : OptionsAccessor(options, mapOf(NAME to "grid")) {

    // todo: check 'name'
    val isGrid: Boolean
        get() = true

    val x: String?
        get() {
            Preconditions.checkState(hasX(), "No facet x specified")
            return getString(X)
        }

    val y: String?
        get() {
            Preconditions.checkState(hasY(), "No facet y specified")
            return getString(Y)
        }

    private fun hasX(): Boolean {
        return has(X)
    }

    private fun hasY(): Boolean {
        return has(Y)
    }

    fun createFacets(dataList: List<DataFrame>): PlotFacets {
        var nameX: String? = null
        val levelsX = LinkedHashSet<Any?>()
        if (hasX()) {
            nameX = x
            for (data in dataList) {
                if (DataFrameUtil.hasVariable(data, nameX!!)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, nameX)
                    levelsX.addAll(DataFrameUtil.distinctValues(data, variable))
                }
            }
        }

        var nameY: String? = null
        val levelsY = LinkedHashSet<Any?>()
        if (hasY()) {
            nameY = y
            for (data in dataList) {
                if (DataFrameUtil.hasVariable(data, nameY!!)) {
                    val variable = DataFrameUtil.findVariableOrFail(data, nameY)
                    levelsY.addAll(DataFrameUtil.distinctValues(data, variable))
                }
            }
        }

        return PlotFacets(nameX, nameY, ArrayList(levelsX), ArrayList(levelsY))
    }
}

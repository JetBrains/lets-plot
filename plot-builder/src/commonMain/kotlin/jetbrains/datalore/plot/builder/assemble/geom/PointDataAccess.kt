package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.interact.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.scale.breaks.QuantitativeTickFormatterFactory

internal class PointDataAccess(private val data: DataFrame,
                               bindings: Map<Aes<*>, VarBinding>) : MappedDataAccess {

    override val mappedAes: Set<Aes<*>> = HashSet(bindings.keys)

    private val myBindings: Map<Aes<*>, VarBinding> = bindings.toMap()

    private val myFormatters = HashMap<Aes<*>, (Any) -> String>()

    override fun isMapped(aes: Aes<*>): Boolean {
        return myBindings.containsKey(aes)
    }

    override fun <T> getMappedData(aes: Aes<T>, index: Int): MappedDataAccess.MappedData<T> {
        checkArgument(isMapped(aes), "Not mapped: $aes")

        val value = valueAfterTransform(aes, index)!!
        @Suppress("UNCHECKED_CAST")
        val scale = myBindings[aes]!!.scale as Scale<T>

        val original = scale.transform.applyInverse(value)
        val s: String
        s = if (original is Number) {
            formatter(aes)(original)
        } else {
            original.toString()
        }

        val continuous = scale.isContinuous

        return MappedDataAccess.MappedData(label(aes), s, continuous)
    }

    private fun label(aes: Aes<*>): String {
        return myBindings[aes]!!.scale!!.name
    }

    private fun valueAfterTransform(aes: Aes<*>, index: Int): Double? {
        val variable = myBindings[aes]!!.variable
        return data.getNumeric(variable)[index]
    }

    private fun formatter(aes: Aes<*>): (Any) -> String {
        if (!myFormatters.containsKey(aes)) {
            myFormatters[aes] = createFormatter(aes)
        }
        return myFormatters[aes]!!
    }

    private fun createFormatter(aes: Aes<*>): (Any) -> String {
        val varBinding = myBindings[aes]
        // only 'stat' or 'transform' vars here
        val `var` = varBinding!!.variable
        var domain = data.range(`var`)
        domain = SeriesUtil.ensureNotZeroRange(domain)
        return QuantitativeTickFormatterFactory.forLinearScale().getFormatter(domain, SeriesUtil.span(domain) / 100.0)
    }
}

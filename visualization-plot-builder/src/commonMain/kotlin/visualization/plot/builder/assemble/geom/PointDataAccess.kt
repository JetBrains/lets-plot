package jetbrains.datalore.visualization.plot.builder.assemble.geom

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.scale.breaks.QuantitativeTickFormatterFactory
import jetbrains.datalore.visualization.plot.builder.VarBinding
import jetbrains.datalore.visualization.plot.common.data.SeriesUtil

internal class PointDataAccess(private val myData: DataFrame, private val bindings: Map<Aes<*>, VarBinding>) : MappedDataAccess {
    private val myBindings: Map<Aes<*>, VarBinding> = bindings.toMap()

    private val myFormatters = HashMap<Aes<*>, (Any) -> String>()

    override val mappedAes: Set<Aes<*>>
        get() = myBindings.keys

    override fun isMapped(aes: Aes<*>): Boolean {
        return myBindings.containsKey(aes)
    }

    override fun <T> getMappedData(aes: Aes<T>, index: Int): MappedDataAccess.MappedData<T> {
        checkArgument(isMapped(aes), "Not mapped: $aes")

        val value = value(aes, index)!!
        @Suppress("UNCHECKED_CAST")
        val scale = myBindings[aes]!!.scale as Scale<T>

        val original = scale.transform.applyInverse(value)
        val s: String
        s = if (original is Number) {
            formatter(aes)(original)
        } else {
            original.toString()
        }

        val aesValue = scale.mapper(value)
        val continuous = scale.isContinuous

        return MappedDataAccess.MappedData(label(aes), s, aesValue, continuous)
    }

    private fun label(aes: Aes<*>): String {
        return myBindings[aes]!!.scale!!.name
    }

    private fun value(aes: Aes<*>, index: Int): Double? {
        val variable = myBindings[aes]!!.variable
        return myData.getNumeric(variable)[index]
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
        var domain = myData.range(`var`)
        domain = SeriesUtil.ensureNotZeroRange(domain)
        return QuantitativeTickFormatterFactory.forLinearScale().getFormatter(domain, SeriesUtil.span(domain) / 100.0)
    }
}

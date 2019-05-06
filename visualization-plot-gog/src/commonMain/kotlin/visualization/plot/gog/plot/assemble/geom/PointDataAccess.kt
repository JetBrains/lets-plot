package jetbrains.datalore.visualization.plot.gog.plot.assemble.geom

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.base.observable.collections.Collections.unmodifiableSet
import jetbrains.datalore.visualization.plot.gog.common.data.SeriesUtil
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.breaks.QuantitativeTickFormatterFactory
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding

internal class PointDataAccess(private val myData: DataFrame, private val myBindings: Map<Aes<*>, VarBinding>) : MappedDataAccess {
    private val myMappedAes: Set<Aes<*>> = unmodifiableSet(myBindings.keys)

    private val myFormatters = HashMap<Aes<*>, (Any) -> String>()

    override val mappedAes: Set<Aes<*>>
        get() = myMappedAes

    override fun isMapped(aes: Aes<*>): Boolean {
        return myBindings.containsKey(aes)
    }

    override fun <T> getMappedData(aes: Aes<T>, index: Int): MappedDataAccess.MappedData<T> {
        checkArgument(isMapped(aes), "Not mapped: $aes")

        val value = value(aes, index)!!
        val scale = myBindings[aes]!!.scale

        val original = scale!!.transform.applyInverse(value)
        val s: String
        s = if (original is Number) {
            formatter(aes)(original)
        } else {
            original.toString()
        }

        val aesValue = scale.mapper!!(value) as T
        val continuous = scale.isContinuous

        return MappedDataAccess.MappedData(label(aes), s, aesValue, continuous)
    }

    private fun label(aes: Aes<*>): String {
        return myBindings[aes]!!.scale!!.name
    }

    protected fun value(aes: Aes<*>, index: Int): Double? {
        val `var` = myBindings[aes]!!.`var`
        return myData.getNumeric(`var`)[index]
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
        val `var` = varBinding!!.`var`
        var domain = myData.range(`var`)
        domain = SeriesUtil.ensureNotZeroRange(domain)
        return QuantitativeTickFormatterFactory.forLinearScale().getFormatter(domain, SeriesUtil.span(domain) / 100.0)
    }
}

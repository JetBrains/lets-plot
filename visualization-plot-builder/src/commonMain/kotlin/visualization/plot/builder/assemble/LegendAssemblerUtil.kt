package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.visualization.plot.base.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.aes.AestheticsBuilder
import jetbrains.datalore.visualization.plot.base.render.Aes
import jetbrains.datalore.visualization.plot.base.render.Aesthetics

internal object LegendAssemblerUtil {
    fun <T> mapToAesthetics(
            valuesByAes: Map<Aes<T>, List<T>>, constantByAes: Map<Aes<T>, T>, aestheticsDefaults: AestheticsDefaults): Aesthetics {
        val builder = AestheticsBuilder(0)
        for (aes in Aes.values()) {
            builder.constantAes(aes as Aes<Any>, aestheticsDefaults.defaultValue(aes))
        }
        for (aes in valuesByAes.keys) {
            val values = valuesByAes[aes]!!
            builder.aes(aes, AestheticsBuilder.collection(values))
            builder.dataPointCount(values.size)
        }
        for (aes in constantByAes.keys) {
            builder.constantAes<T>(aes, constantByAes[aes]!!)
        }
        return builder.build()
    }


    fun mapToAesthetics(
            valueByAesIterable: Collection<Map<Aes<*>, Any>>, constantByAes: Map<Aes<*>, Any>, aestheticsDefaults: AestheticsDefaults): Aesthetics {
        val dataPoints = ArrayList<Map<Aes<*>, Any>>()
        for (valueByAes in valueByAesIterable) {
            val dataPoint = HashMap<Aes<*>, Any>()
            for (aes in Aes.values()) {
                dataPoint[aes] = aestheticsDefaults.defaultValueInLegend(aes)!!
            }
            for (aes in valueByAes.keys) {
                dataPoint[aes] = valueByAes[aes]!!
            }
            /*
      Disabled because 'size'-related constants are often looks ugly in legend
      for (Aes aes : constantByAes.keySet()) {
        dataPoint.put(aes, constantByAes.get(aes));
      }
      */
            dataPoints.add(dataPoint)
        }

        val builder = AestheticsBuilder(dataPoints.size)
        for (aes in Aes.values()) {
            val aes1 = aes as Aes<Any>
            builder.aes(aes1) { index -> dataPoints[index][aes]!! }
        }
        return builder.build()
    }
}

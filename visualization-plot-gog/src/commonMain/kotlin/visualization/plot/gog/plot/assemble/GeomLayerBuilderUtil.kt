package jetbrains.datalore.visualization.plot.gog.plot.assemble

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil.transformVarFor
import jetbrains.datalore.visualization.plot.gog.core.data.Stat
import jetbrains.datalore.visualization.plot.gog.core.data.stat.Stats
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.plot.VarBinding
import jetbrains.datalore.visualization.plot.gog.plot.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProviderHelper

object GeomLayerBuilderUtil {

    fun handledAes(geomProvider: GeomProvider, stat: Stat): List<Aes<*>> {
        val handledAes = LinkedHashSet(geomProvider.renders())
        handledAes.addAll(stat.requires())
        return ArrayList(handledAes)
    }

    /*
  public static boolean isOriginalData(DataFrame data) {
    for (DataFrame.Variable var : data.variables()) {
      if (!var.isOrigin()) {
        return false;
      }
    }
    return true;
  }
  */

    fun rewireBindingsAfterStat(
            data: DataFrame, stat: Stat, bindings: List<VarBinding>, scaleProviderByAes: TypedScaleProviderMap): MutableMap<Aes<*>, VarBinding> {

        // finalize deferred bindings
        val bindingsByAes = HashMap<Aes<*>, VarBinding>()
        for (binding in bindings) {
            var _binding = binding
            if (_binding.isDeferred) {
                // finalize deferred binding
                _binding = _binding.bindDeferred(data)
            }
            bindingsByAes[_binding.aes] = _binding
        }

        // no 'origin' variables beyond this point
        for (binding in bindings) {
            if (binding.`var`.isOrigin) {
                val aes = binding.aes
                val transformVar = transformVarFor(aes)
                bindingsByAes[aes] = VarBinding(transformVar, aes, binding.scale)
            }
        }

        // re-bind variables if not 'identity' stat
        val defStatMapping = Stats.defaultMapping(stat)

        if (!defStatMapping.isEmpty()) {
            // apply stat's default mappings
            val statVarByBoundVar = HashMap<DataFrame.Variable, DataFrame.Variable>()
            for (aes in defStatMapping.keys) {
                val statVar = defStatMapping[aes]!!
                // Add binding if not there
                if (!bindingsByAes.containsKey(aes)) {
                    val scale = ScaleProviderHelper.getOrCreateDefault(aes, scaleProviderByAes)
                            .createScale(data, statVar)
                    bindingsByAes[aes] = VarBinding(statVar, aes, scale)
                }
            }
        }

        return bindingsByAes
    }
}

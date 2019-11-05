/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Stat
import jetbrains.datalore.plot.base.data.DataFrameUtil.transformVarFor
import jetbrains.datalore.plot.base.stat.Stats
import jetbrains.datalore.plot.builder.VarBinding
import jetbrains.datalore.plot.builder.scale.ScaleProviderHelper

object GeomLayerBuilderUtil {

    fun handledAes(geomProvider: jetbrains.datalore.plot.builder.assemble.geom.GeomProvider, stat: Stat): List<Aes<*>> {
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
        data: DataFrame, stat: Stat, bindings: List<VarBinding>, scaleProviderByAes: TypedScaleProviderMap
    ): MutableMap<Aes<*>, VarBinding> {

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
            if (binding.variable.isOrigin) {
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

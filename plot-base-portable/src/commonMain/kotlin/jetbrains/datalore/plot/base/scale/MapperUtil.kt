/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.scale

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.Transform
import kotlin.math.max
import kotlin.math.min

object MapperUtil {
    fun map(r: ClosedRange<Double>, mapper: (Double?) -> Double?): ClosedRange<Double> {
        val a = mapper(r.lowerEndpoint())!!
        val b = mapper(r.upperEndpoint())!!
        return ClosedRange(min(a, b), max(a, b))
    }

    fun mapDiscreteDomainValuesToNumbers(values: Collection<*>): Map<Any, Double> {
        // Rollback this improvement due to:
        // DP-4956 Discrete scale looks strange
        /*
    SeriesUtil.CheckedDoubleIterable checkedDoubles = SeriesUtil.checkedDoubles(values);
    if (checkedDoubles.notEmptyAndCanBeCast()) {
      // if serie contain numbers then preserve original values because
      // if this serie is also bound with another aes and continuous mapper is created elsewhere
      // then those two mappers (scales) will work consistently (transform/inverseTransform)
      Map<Object, Double> result = new LinkedHashMap<>();
      for (Double v : checkedDoubles.cast()) {
        if (v != null) {
          result.put(v, v);
        }
      }
      return result;
    }
    */
        return mapDiscreteDomainValuesToIndices(values)
    }

    private fun mapDiscreteDomainValuesToIndices(values: Collection<*>): Map<Any, Double> {
        val result = LinkedHashMap<Any, Double>()
        var index = 0
        for (v in values) {
            if (v != null && !result.containsKey(v)) {
                result[v] = index++.toDouble()
            }
        }
        return result
    }

    fun rangeWithLimitsAfterTransform(
        data: DataFrame, variable: DataFrame.Variable, lowerLimit: Double?, upperLimit: Double?, trans: Transform?): ClosedRange<Double> {
        val lower = lowerLimit ?: data.range(variable)!!.lowerEndpoint()
        val upper = upperLimit ?: data.range(variable)!!.upperEndpoint()
        val limits = ArrayList<Double>()
        limits.add(lower)
        limits.add(upper)
        return ClosedRange.encloseAll(trans?.apply(limits) ?: limits)
    }
}

/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.scale

import org.jetbrains.letsPlot.commons.intern.datetime.TimeZone
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.commons.time.interval.NiceTimeInterval
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.scale.breaks.DateTimeBreaksGen

object ScaleProviderHelper {
    fun <T> createDefault(aes: Aes<T>): ScaleProvider {
        return ScaleProviderBuilder(aes).build()
    }

    fun <T> createDateTimeScaleProviderBuilder(
        aes: Aes<T>,
        dataType: DataType,
        tz: TimeZone?
    ): ScaleProviderBuilder<T> {
        return ScaleProviderBuilder(aes)
            .breaksGenerator(
                DateTimeBreaksGen(
                    minInterval = NiceTimeInterval.minIntervalOf(dataType),
                    maxInterval = NiceTimeInterval.maxIntervalOf(dataType),
                    tz = tz,
                )
            )
    }
}

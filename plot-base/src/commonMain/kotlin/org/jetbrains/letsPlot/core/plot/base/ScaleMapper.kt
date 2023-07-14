/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

// :(( Implementing function interface is prohibited in JavaScript
// We can't implement ScaleMapper (in GuideMapper(s)) this way.
// :

//typealias ScaleMapper<TargetT> = (Double?) -> TargetT?

interface ScaleMapper<TargetT> {
    operator fun invoke(v: Double?): TargetT?

    companion object {
        fun <T> wrap(f: (Double?) -> T): ScaleMapper<T> {
            return object : ScaleMapper<T> {
                override fun invoke(v: Double?): T? {
                    return f(v)
                }
            }
        }

        fun <T> wrap(f: (Double?) -> T?, defaultValue: T): ScaleMapper<T> {
            return object : ScaleMapper<T> {
                override fun invoke(v: Double?): T? {
                    return f(v) ?: defaultValue
                }
            }
        }

        fun <T> wrap(f: ScaleMapper<T>, defaultValue: T): ScaleMapper<T> {
            return object : ScaleMapper<T> {
                override fun invoke(v: Double?): T? {
                    return f(v) ?: defaultValue
                }
            }
        }
    }
}

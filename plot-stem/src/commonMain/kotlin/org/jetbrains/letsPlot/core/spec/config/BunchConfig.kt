/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.spec.Option.GGBunch
import org.jetbrains.letsPlot.core.spec.Option.GGBunch.Item

class BunchConfig(opts: Map<String, Any>) : OptionsAccessor(opts) {
    private val myItems = ArrayList<BunchItem>()

    val bunchItems: List<BunchItem>
        get() = myItems

    init {

        val items = getList(GGBunch.ITEMS)
        for (itemRaw in items) {
            if (itemRaw is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val itemOptions = OptionsAccessor(itemRaw as MutableMap<String, Any>)
                myItems.add(
                    BunchItem(
                        itemOptions.getMap(Item.FEATURE_SPEC),
                        itemOptions.getDouble(Item.X)!!,
                        itemOptions.getDouble(Item.Y)!!,
                        itemOptions.getDouble(Item.WIDTH),
                        itemOptions.getDouble(Item.HEIGHT)
                    )
                )
            }
        }
    }

    class BunchItem(
        private val myFeatureSpec: Map<*, *>,
        val x: Double,
        val y: Double,
        private val myWidth: Double?,
        private val myHeight: Double?
    ) {

        val featureSpec: Map<String, Any>
            @Suppress("UNCHECKED_CAST")
            get() = myFeatureSpec as Map<String, Any>

        val size: DoubleVector
            get() {
                check(hasSize()) { "Size is not defined" }
                return DoubleVector(myWidth!!, myHeight!!)
            }

        fun hasSize(): Boolean {
            return myWidth != null && myHeight != null
        }
    }
}

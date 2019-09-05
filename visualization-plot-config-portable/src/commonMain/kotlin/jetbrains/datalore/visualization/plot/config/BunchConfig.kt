package jetbrains.datalore.visualization.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.config.Option.GGBunch
import jetbrains.datalore.visualization.plot.config.Option.GGBunch.Item

class BunchConfig(opts: Map<*, *>) : OptionsAccessor(opts) {
    private val myItems = ArrayList<BunchItem>()

    val bunchItems: List<BunchItem>
        get() = myItems

    init {

        val items = getList(GGBunch.ITEMS)
        for (itemRaw in items) {
            if (itemRaw is Map<*, *>) {
                val itemOptions = OptionsAccessor(itemRaw as MutableMap<*, *>)
                myItems.add(BunchItem(
                        itemOptions.getMap(Item.FEATURE_SPEC),
                        itemOptions.getDouble(Item.X)!!,
                        itemOptions.getDouble(Item.Y)!!,
                        itemOptions.getDouble(Item.WIDTH),
                        itemOptions.getDouble(Item.HEIGHT)
                ))
            }
        }
    }

    class BunchItem(private val myFeatureSpec: Map<*, *>, val x: Double, val y: Double, private val myWidth: Double?, private val myHeight: Double?) {

        val featureSpec: Map<String, Any>
            get() = myFeatureSpec as Map<String, Any>

        val size: DoubleVector
            get() {
                checkState(hasSize(), "Size is not defined")
                return DoubleVector(myWidth!!, myHeight!!)
            }

        fun hasSize(): Boolean {
            return myWidth != null && myHeight != null
        }
    }
}

package jetbrains.datalore.visualization.plot.core

import jetbrains.datalore.base.typedKey.TypedKeyContainer
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.plot.gog.core.aes.AesInitValue
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.render.geom.LivemapGeom

open class AestheticsDefaults protected constructor() {

    private val myDefaults: TypedKeyContainer
    // ?TypedKeyContainer
    private val myDefaultsInLegend = TypedKeyHashMap()

    init {
        myDefaults = TypedKeyHashMap()
        for (aes in Aes.values()) {
            // Safe cast because AesInitValue.get(aes) is guaranteed to return correct type.
            myDefaults.put(aes as Aes<Any>, AesInitValue[aes])
        }
    }

    protected fun <T> update(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        myDefaults.put(aes, defaultValue)
        return this
    }

    protected fun <T> updateInLegend(aes: Aes<T>, defaultValue: T): AestheticsDefaults {
        myDefaultsInLegend.put(aes, defaultValue)
        return this
    }


    open fun rangeIncludesZero(aes: Aes<*>): Boolean {
        return false
    }

    fun <T> defaultValue(aes: Aes<T>): T {
        return myDefaults[aes]
    }

    fun <T> defaultValueInLegend(aes: Aes<T>): T {
        return if (myDefaultsInLegend.containsKey(aes)) {
            myDefaultsInLegend[aes]
        } else defaultValue(aes)
    }

    companion object {
        fun point(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.SIZE, 2.0)
                    .updateInLegend(Aes.SIZE, 5.0)
        }

        fun path(): AestheticsDefaults {
            return base()
        }

        fun line(): AestheticsDefaults {
            return path()
        }

        fun abline(): AestheticsDefaults {
            return path()
        }

        fun hline(): AestheticsDefaults {
            return path()
        }

        fun vline(): AestheticsDefaults {
            return path()
        }

        fun smooth(): AestheticsDefaults {
            return path()
                    .update(Aes.COLOR, Color.MAGENTA)
                    .update(Aes.FILL, Color.BLACK)
        }

        fun bar(): AestheticsDefaults {
            return object : AestheticsDefaults() {
                override fun rangeIncludesZero(aes: Aes<*>): Boolean {
                    return aes == Aes.Y || super.rangeIncludesZero(aes)
                }
            }
                    .update<Double>(Aes.WIDTH, 0.9)
                    .update(Aes.COLOR, Color.TRANSPARENT)    // no outline (transparent)
        }

        fun histogram(): AestheticsDefaults {
            return object : AestheticsDefaults() {
                override fun rangeIncludesZero(aes: Aes<*>): Boolean {
                    return aes == Aes.Y || super.rangeIncludesZero(aes)
                }
            }
                    .update<Color>(Aes.COLOR, Color.TRANSPARENT)    // no outline (transparent)
        }

        fun tile(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.COLOR, Color.TRANSPARENT)    // no outline (transparent)
        }

        fun errorBar(): AestheticsDefaults {
            return AestheticsDefaults()
        }

        fun polygon(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.COLOR, Color.TRANSPARENT)    // no outline (transparent)
        }

        fun map(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.SIZE, 0.2)                    // outline thickness
                    .update(Aes.COLOR, Color.GRAY)
                    .update(Aes.FILL, Color.TRANSPARENT)
        }

        fun boxplot(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.WIDTH, 0.9)
                    .update(Aes.COLOR, Color.BLACK)
                    .update(Aes.FILL, Color.WHITE)
        }

        fun livemap(displayMode: LivemapGeom.DisplayMode, scaled: Boolean): AestheticsDefaults {
            when (displayMode) {
                LivemapGeom.DisplayMode.POLYGON -> return polygon()
                LivemapGeom.DisplayMode.POINT -> return point()
                        .updateInLegend(Aes.SIZE, 5.0)
                LivemapGeom.DisplayMode.BAR -> return base()
                        .update(Aes.SIZE, 40.0)
                        .update(Aes.COLOR, Color.TRANSPARENT)
                LivemapGeom.DisplayMode.PIE -> return base()
                        .update(Aes.SIZE, 20.0)
                        .update(Aes.COLOR, Color.TRANSPARENT)
                        .updateInLegend(Aes.SIZE, 5.0)
                LivemapGeom.DisplayMode.HEATMAP -> return base().update(Aes.SIZE, if (scaled) 0.01 else 10.0)
                else -> throw IllegalArgumentException("Defaults are not available for display mode with name: " + displayMode.name)
            }
        }

        fun ribbon(): AestheticsDefaults {
            return base()
        }

        fun area(): AestheticsDefaults {
            return object : AestheticsDefaults() {
                override fun rangeIncludesZero(aes: Aes<*>): Boolean {
                    return aes == Aes.Y || super.rangeIncludesZero(aes)
                }
            }
        }

        fun density(): AestheticsDefaults {
            return area()
                    .update(Aes.FILL, Color.TRANSPARENT)
        }

        fun contour(): AestheticsDefaults {
            return path()
        }

        fun contourf(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.SIZE, 0.0)
        }

        fun density2d(): AestheticsDefaults {
            return contour()
        }

        fun density2df(): AestheticsDefaults {
            return contourf()
        }

        fun jitter(): AestheticsDefaults {
            return point()
        }

        fun freqpoly(): AestheticsDefaults {
            return path()
        }

        fun step(): AestheticsDefaults {
            return path()
        }

        fun rect(): AestheticsDefaults {
            return polygon()
        }

        fun segment(): AestheticsDefaults {
            return path()
        }

        fun text(): AestheticsDefaults {
            return AestheticsDefaults()
                    .update(Aes.SIZE, 7.0)
                    .update(Aes.COLOR, Color.parseHex("#3d3d3d")) // dark gray
        }


        fun raster(): AestheticsDefaults {
            return base()
        }

        fun image(): AestheticsDefaults {
            return base()
        }

        private fun base(): AestheticsDefaults {
            return AestheticsDefaults()
        }
    }
}

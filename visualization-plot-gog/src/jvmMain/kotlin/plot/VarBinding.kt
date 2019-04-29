package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import jetbrains.datalore.visualization.plot.gog.core.scale.Scale2
import jetbrains.datalore.visualization.plot.gog.plot.scale.ScaleProvider

open class VarBinding// ToDo: ?Type VarBinding can be generified
(val `var`: DataFrame.Variable, val aes: Aes<*>, open val scale: Scale2<*>?) {

    open val isDeferred: Boolean
        get() = false

    open fun bindDeferred(data: DataFrame): VarBinding {
        throw IllegalStateException("Not a deferred var binding")
    }

    override fun toString(): String {
        return "VarBinding{" +
                "myVar=" + `var` +
                ", myAes=" + aes +
                ", myScale=" + scale +
                ", deferred=" + isDeferred +
                '}'.toString()
    }

    companion object {
        fun deferred(`var`: DataFrame.Variable, aes: Aes<*>, scaleProvider: ScaleProvider<*>): VarBinding {
            return object : VarBinding(`var`, aes, null) {
                override val scale: Scale2<*>
                    get() = throw IllegalStateException("Scale not defined for deferred var binding")

                override val isDeferred: Boolean
                    get() = true

                override fun bindDeferred(data: DataFrame): VarBinding {
                    // ToDo: remove this method because the scale is only created in the client config and
                    // 'stat' var must be present (stat vars are added on server side)
                    val scale = scaleProvider.createScale(data, `var`)
                    return VarBinding(`var`, aes, scale)
                }
            }
        }
    }
}

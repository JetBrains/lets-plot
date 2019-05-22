package jetbrains.datalore.visualization.plot.builder

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.Scale
import jetbrains.datalore.visualization.plot.builder.scale.ScaleProvider

// ToDo: ?Type VarBinding can be generified
open class VarBinding(val variable: DataFrame.Variable, val aes: Aes<*>, open val scale: Scale<*>?) {

    open val isDeferred: Boolean
        get() = false

    open fun bindDeferred(data: DataFrame): VarBinding {
        throw IllegalStateException("Not a deferred var binding")
    }

    override fun toString(): String {
        return "VarBinding{" +
                "variable=" + variable +
                ", aes=" + aes +
                ", scale=" + scale +
                ", deferred=" + isDeferred +
                '}'.toString()
    }

    companion object {
        fun deferred(variable: DataFrame.Variable, aes: Aes<*>, scaleProvider: ScaleProvider<*>): VarBinding {
            return object : VarBinding(variable, aes, null) {
                override val scale: Scale<*>
                    get() = throw IllegalStateException("Scale not defined for deferred var binding")

                override val isDeferred: Boolean
                    get() = true

                override fun bindDeferred(data: DataFrame): VarBinding {
                    // ToDo: remove this method because the scale is only created in the client config and
                    // 'stat' var must be present (stat vars are added on server side)
                    val scale = scaleProvider.createScale(data, variable)
                    return VarBinding(variable, aes, scale)
                }
            }
        }
    }
}

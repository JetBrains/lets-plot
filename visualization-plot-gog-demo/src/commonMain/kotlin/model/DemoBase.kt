package jetbrains.datalore.visualization.gogDemo.model

import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.jvm.JvmOverloads

open class DemoBase @JvmOverloads constructor(demoBoxSize: DoubleVector = DEFAULT_DEMO_BOX_SIZE) {

    protected open val viewSize: DoubleVector = toViewSize(demoBoxSize)

    companion object {
        private val PADDING = DoubleVector(20.0, 20.0)
        private val DEFAULT_DEMO_BOX_SIZE = DoubleVector(400.0, 300.0)

        fun toViewSize(innerSize: DoubleVector): DoubleVector {
            return innerSize.add(PADDING.mul(2.0))
        }
    }
}

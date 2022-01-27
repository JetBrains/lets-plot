/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.core.graphics

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.canvas.Context2d
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class RenderBox : RenderObject {
    protected lateinit var graphics: GraphicsService
    var attached: Boolean = false
        private set

    var isDirty = true
        set(value) {
            if (value != field) {
                if (value) {
                    graphics.repaint()
                }

                field = value
            }
        }

    open var origin by visualProp(DoubleVector.ZERO)
    open var dimension by visualProp(DoubleVector.ZERO)

    fun attach(graphics: GraphicsService) {
        require(!attached) // Most likely logical error
        attached = true
        this.graphics = graphics
        onAttach()
    }

    protected open fun onAttach() {}
    protected open fun updateState() {}
    protected abstract fun renderInternal(ctx: Context2d)

    final override fun render(ctx: Context2d) {
        if (isDirty) {
            updateState()
            isDirty = false
        }

        renderInternal(ctx)
    }

    public fun <T: RenderBox> T.setState(block: T.() -> Unit): T {
        block()
        updateState()
        isDirty = false
        return this
    }

    fun <T, TValue> T.visualProp(initialValue: TValue, onBeforeChange: (TValue) -> Unit = {}): ReadWriteProperty<T, TValue> {
        return object : ReadWriteProperty<T, TValue> {
            private var value = initialValue

            override fun getValue(thisRef: T, property: KProperty<*>): TValue {
                return value
            }
            override fun setValue(thisRef: T, property: KProperty<*>, value: TValue) {
                if (value != this.value) {
                    onBeforeChange(value)
                    this.value = value
                    isDirty = true
                }
            }
        }
    }
}

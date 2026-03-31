package org.jetbrains.letsPlot.raster.scene

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Attribute


internal class VariableAttribute<T>(
    private val attributeSpec: VariableAttributeSpec<T>,
    private var value: T,
    private val onChange: (AttributeObject, AttributeSpec, Any?, Any?) -> Unit
) : ReadWriteProperty<AttributeObject, T>, Attribute {
    var isDefault = true
        private set

    override fun getValue(thisRef: AttributeObject, property: KProperty<*>): T = value

    override fun setValue(thisRef: AttributeObject, property: KProperty<*>, value: T) {
        setValueInternal(thisRef, value)
        isDefault = false
    }

    fun inheritValue(thisRef: AttributeObject, value: T) {
        if (!isDefault) {
            return
        }

        setValueInternal(thisRef, value = value)
    }

    private fun setValueInternal(thisRef: AttributeObject, value: T) {
        val oldValue = this.value
        if (oldValue != value) {
            this.value = value
            onChange(thisRef, attributeSpec, oldValue, value)
        }
    }
}


internal class DerivedAttribute<T>(
    private val attributeSpec: DerivedAttributeSpec<T>,
    private val valueProvider: () -> T,
    private val onChange: (AttributeObject, AttributeSpec, T, T) -> Unit
) : ReadOnlyProperty<AttributeObject, T>, Attribute {
    private var isDirty = true
    private var value: T? = null

    fun invalidate() {
        isDirty = true
    }

    override fun getValue(thisRef: AttributeObject, property: KProperty<*>): T {
        if (isDirty) {
            val oldValue = value
            val newValue = valueProvider()
            value = newValue
            isDirty = false
            if (oldValue != null && oldValue != newValue) {
                @Suppress("UNCHECKED_CAST")
                onChange(thisRef, attributeSpec, oldValue as T, newValue)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}


internal class ResolvedAttribute<T>(
    private val attributeSpec: ResolvedAttributeSpec<T>,
    private val compute: (Any?) -> T
) : ReadOnlyProperty<AttributeObject, T>, Attribute {
    private var cache: T? = null

    // State tracking
    private var lastParentVersion = -1
    private var lastDepsVersion = -1

    var version = 0; private set

    override fun getValue(thisRef: AttributeObject, property: KProperty<*>): T {
        return getInternalValue(thisRef)
    }

    fun getInternalValue(owner: AttributeObject): T {
        val parent = owner.parent
        val parentAttribute = parent?.attributes?.get<ResolvedAttribute<T>>(attributeSpec)

        // 1. Force Upstream Update (Pull)
        parentAttribute?.getInternalValue(parent)

        // 2. Resolve Versions
        val currentParentVersion = parentAttribute?.version ?: 0
        val currentLocalVersion = owner.attributes.pullVersion(attributeSpec)

        // 3. Check Staleness
        if (cache == null ||
            currentLocalVersion != lastDepsVersion ||
            currentParentVersion != lastParentVersion
        ) {
            // 4. Fetch Parent Value
            val parentValue = parentAttribute?.getCurrentValue()

            val newValue = compute(parentValue)

            if (cache != newValue) {
                cache = newValue
                version++
            }

            lastParentVersion = currentParentVersion
            lastDepsVersion = currentLocalVersion
        }

        return cache!!
    }

    fun getCurrentValue(): T? = cache
}
/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface Managed {
    fun close()
    fun isClosed(): Boolean
}

internal abstract class Node {
    var id: String? = null
    private val visualPropInstances = mutableMapOf<KProperty<*>, VisualProperty<*>>()
    private val computedPropInstances = mutableMapOf<KProperty<*>, ComputedProperty<*>>()
    private val computedPropDependencies = mutableMapOf<KProperty<*>, Set<KProperty<*>>>() // direct and transitive dependencies
    private val propFinalizers = mutableMapOf<KProperty<*>, () -> Managed?>()

    var href: String? = null

    var isVisible: Boolean by visualProp(true)
    var opacity: Float? by visualProp(null)

    // Set value from parent if not set explicitly
    internal fun <TValue> inheritValue(prop: KProperty<*>, value: TValue) {
        val (kProp, visProp) = visualPropInstances.entries.firstOrNull { it.key.name == prop.name } ?: return

        @Suppress("UNCHECKED_CAST")
        val theProp: VisualProperty<TValue> = (visProp as? VisualProperty<TValue>) ?: return
        if (theProp.isDefault) {
            theProp.setValue(this, kProp, value)
        }
    }


    internal fun invalidateComputedProp(prop: KProperty<*>) {
        val computedPropInstance = computedPropInstances[prop]  ?: error { "Class `${this::class.simpleName}` doesn't have computedProperty `${prop.name}`" }
        computedPropInstance.invalidate()
    }

    private fun handlePropertyChange(property: KProperty<*>, oldValue: Any?, newValue: Any?) {
        if (property in propFinalizers && oldValue is Managed) {
            oldValue.close()
        }

        onPropertyChanged(property)

        computedPropDependencies
            .filter { (_, deps) -> property in deps }
            .forEach { (dependedProperty, _) -> invalidateComputedProp(dependedProperty) }
    }

    fun <T> computedProp(
        vararg deps: KProperty<*>,
        valueProvider: () -> T
    ): PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> {
        return computedProp(dependencies = deps, managed = false, valueProvider)
    }

    fun <T> computedProp(
        vararg dependencies: KProperty<*>,
        managed: Boolean,
        valueProvider: () -> T,
    ): PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> {
        return PropertyDelegateProvider<Node, ReadOnlyProperty<Node, T>> { thisRef, property ->
            val computedProperty = ComputedProperty(valueProvider, thisRef::handlePropertyChange)
            computedPropInstances[property] = computedProperty

            val fullDeps = dependencies.map { directDep ->
                when (directDep) {
                    in computedPropDependencies -> computedPropDependencies[directDep]!!
                    in visualPropInstances -> setOf(directDep)
                    else -> error("Missing dependency: ${directDep.name}. All dependencies must be defines before")
                }
            }.flatten()

            computedPropDependencies[property] = fullDeps.toSet()

            if (managed) {
                propFinalizers[property] = { computedProperty.getValue(thisRef, property) as Managed? }
            }

            return@PropertyDelegateProvider computedProperty
        }
    }

    fun <T> visualProp(
        initialValue: T,
        managed: Boolean = false
    ): PropertyDelegateProvider<Node, ReadWriteProperty<Node, T>> {
        return PropertyDelegateProvider<Node, ReadWriteProperty<Node, T>> { thisRef, property ->
            val visualProperty = VisualProperty(initialValue, thisRef::handlePropertyChange)
            visualPropInstances[property] = visualProperty

            if (managed) {
                propFinalizers[property] = { visualProperty.getValue(thisRef, property) as Managed? }
            }

            return@PropertyDelegateProvider visualProperty
        }
    }

    fun release() {
        propFinalizers
            .values
            .mapNotNull { it() }
            .filterNot(Managed::isClosed)
            .forEach(Managed::close)
    }

    protected open fun onPropertyChanged(prop: KProperty<*>) {}
    protected open fun repr(): String? = null

    override fun toString(): String {
        val idStr = id?.let { "id: '$it' " } ?: ""
        return "class: ${this::class.simpleName}$idStr${repr()}"
    }
}

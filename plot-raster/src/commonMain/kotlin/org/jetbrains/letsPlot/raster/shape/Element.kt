/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal abstract class Element {
    // Version counters
    var localTransformVersion = 0
        private set
    var geometryVersion = 0
        private set

    private val hierarchicalPropInstances = mutableMapOf<KProperty<*>, HierarchicalProperty<*, *>>()
    private val visualPropInstances = mutableMapOf<KProperty<*>, VisualProperty<*>>()
    private val computedPropInstances = mutableMapOf<KProperty<*>, ComputedProperty<*>>()
    private val computedPropDependencies = mutableMapOf<KProperty<*>, Set<KProperty<*>>>() // direct and transitive dependencies

    var id: String? = null
    var transform: AffineTransform by visualProp(AffineTransform.IDENTITY)
    var styleClass: List<String>? by visualProp(null)
    var parent: Container? by visualProp(null)
    val parents: List<Container> by computedProp(Element::parent) {
        (parent?.parents ?: emptyList()) + listOfNotNull(parent)
    }

    var bufferedRendering: Boolean by visualProp(false)
    var isDirty: Boolean = true

    var href: String? = null
    var isVisible: Boolean by visualProp(true)
    var opacity: Float? by visualProp(null)
    var clipPath: Path2d? by visualProp(null)
    var isMouseTransparent: Boolean = true
    var peer: SvgCanvasPeer? by visualProp(null)

    // CTM depends on Transform Version
    val ctm: AffineTransform by hierarchicalProp(Element::ctm, { localTransformVersion }) { parentCtm ->
        (parentCtm ?: AffineTransform.IDENTITY).concat(transform)
    }

    // bBoxLocal is Computed (Cached)
    val bBoxLocal: DoubleRectangle by computedProp(Element::transform) {
        calculateLocalBBox()
    }

    // bBoxGlobal depends on CTM (Parent) AND Geometry/Transform (Local)
    val bBoxGlobal: DoubleRectangle by hierarchicalProp(Element::ctm, { localTransformVersion + geometryVersion }) { _ ->
        ctm.transform(bBoxLocal)
    }

    protected abstract fun calculateLocalBBox(): DoubleRectangle

    open fun render(ctx: Context2d) {}

    open fun repr(): String? {
        return ", ctm: ${ctm.repr()}, $bBoxGlobal"
    }

    fun detach() {
        onDetach()
    }

    protected open fun onDetach() {}

    fun <T, P> hierarchicalProp(
        dependencyProperty: KProperty<P>,
        localVersionProvider: () -> Int,
        compute: (P?) -> T
    ): PropertyDelegateProvider<Element, ReadOnlyProperty<Element, T>> {
        return PropertyDelegateProvider { thisRef, property ->
            val hp = HierarchicalProperty(thisRef, dependencyProperty, localVersionProvider, compute)
            thisRef.hierarchicalPropInstances[property] = hp
            ReadOnlyProperty { _, _ -> hp.getValue() }
        }
    }

    fun <T> computedProp(
        vararg dependencies: KProperty<*>,
        valueProvider: () -> T,
    ): PropertyDelegateProvider<Element, ReadOnlyProperty<Element, T>> {
        return PropertyDelegateProvider<Element, ReadOnlyProperty<Element, T>> { thisRef, property ->
            val computedProperty = ComputedProperty(valueProvider, thisRef::handlePropertyChange)
            computedPropInstances[property] = computedProperty

            val fullDeps = dependencies.flatMap { directDep ->
                when (directDep) {
                    in computedPropDependencies -> computedPropDependencies[directDep]!!
                    in visualPropInstances -> setOf(directDep)
                    else -> error("Missing dependency: ${directDep.name}")
                }
            }
            computedPropDependencies[property] = fullDeps.toSet()
            return@PropertyDelegateProvider computedProperty
        }
    }

    fun <T> visualProp(initialValue: T): PropertyDelegateProvider<Element, ReadWriteProperty<Element, T>> {
        return PropertyDelegateProvider<Element, ReadWriteProperty<Element, T>> { thisRef, property ->
            val visualProperty = VisualProperty(initialValue, thisRef::handlePropertyChange)
            visualPropInstances[property] = visualProperty
            return@PropertyDelegateProvider visualProperty
        }
    }


    fun getHierarchicalProperty(property: KProperty<*>): HierarchicalProperty<*, *> {
        return hierarchicalPropInstances[property] ?: error {
            "Class `${this::class.simpleName}` doesn't have hierarchicalProperty `${property.name}`"
        }
    }


    protected open fun onPropertyChanged(prop: KProperty<*>) {
        if (prop == Element::transform) {
            localTransformVersion++
            parent?.invalidateGeometry()
            markDirty()
        }
    }

    internal fun <TValue> inheritValue(prop: KProperty<*>, value: TValue) {
        val (kProp, visProp) = visualPropInstances.entries.firstOrNull { it.key.name == prop.name } ?: return
        @Suppress("UNCHECKED_CAST")
        val theProp: VisualProperty<TValue> = (visProp as? VisualProperty<TValue>) ?: return
        if (theProp.isDefault) {
            theProp.setValue(this, kProp, value)
        }
    }

    internal fun invalidateComputedProp(prop: KProperty<*>) {
        val computedPropInstance = computedPropInstances[prop]
            ?: error { "Class `${this::class.simpleName}` doesn't have computedProperty `${prop.name}`" }
        computedPropInstance.invalidate()
    }

    private fun handlePropertyChange(property: KProperty<*>, oldValue: Any?, newValue: Any?) {
        if (property != Element::ctm) {
            markDirty()
        }
        onPropertyChanged(property)
        computedPropDependencies
            .filter { (_, deps) -> property in deps }
            .forEach { (dependedProperty, _) -> invalidateComputedProp(dependedProperty) }
    }

    internal fun markDirty() {
        if (isDirty) return
        isDirty = true
        parent?.markDirty()
    }

    // Call this when bBoxLocal changes (e.g., resizing)
    internal fun invalidateGeometry() {
        geometryVersion++
        invalidateComputedProp(Element::bBoxLocal)
        parent?.invalidateGeometry()
    }

    override fun toString(): String {
        val idStr = id?.let { "id: '$it' " } ?: ""
        return "class: ${this::class.simpleName}$idStr${repr()}"
    }
}
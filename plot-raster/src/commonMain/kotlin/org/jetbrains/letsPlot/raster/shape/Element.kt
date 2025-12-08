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


internal abstract class Element() {
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

    var outputCache: Boolean by visualProp(false)

    // Internal dirty flag. Defaults to true so we render at least once.
    var isDirty: Boolean = true

    var href: String? = null
    var isVisible: Boolean by visualProp(true)
    var opacity: Float? by visualProp(null)
    var clipPath: Path2d? by visualProp(null)
    var isMouseTransparent: Boolean = true // need proper hitTest for non-rectangular shapes for correct default "false"

    var peer: SvgCanvasPeer? by visualProp(null)

    fun <T> computedProp(
        vararg dependencies: KProperty<*>,
        valueProvider: () -> T,
    ): PropertyDelegateProvider<Element, ReadOnlyProperty<Element, T>> {
        return PropertyDelegateProvider<Element, ReadOnlyProperty<Element, T>> { thisRef, property ->
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

    protected open fun onPropertyChanged(prop: KProperty<*>) {}

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
        markDirty()

        onPropertyChanged(property)

        computedPropDependencies
            .filter { (_, deps) -> property in deps }
            .forEach { (dependedProperty, _) -> invalidateComputedProp(dependedProperty) }
    }

    // Marks this element as dirty and notifies parents if they are caching.
    internal fun markDirty() {
        if (isDirty) return // Stop recursion, already dirty
        isDirty = true

        // Bubbling: If my parent is caching, it needs to know its content is invalid.
        parent?.markDirty()
    }

    override fun toString(): String {
        val idStr = id?.let { "id: '$it' " } ?: ""
        return "class: ${this::class.simpleName}$idStr${repr()}"
    }

    // Not affected by org.jetbrains.skiko.SkiaLayer.getContentScale
    // (see org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView.onRender)
    val ctm: AffineTransform by computedProp(Element::parent, Element::transform) {
        val parentCtm = parent?.ctm ?: AffineTransform.IDENTITY
        parentCtm.concat(transform)
    }

    open val localBounds: DoubleRectangle = DoubleRectangle.XYWH(0, 0, 0, 0)

    // Not affected by org.jetbrains.skiko.SkiaLayer.getContentScale
    // (see org.jetbrains.letsPlot.skia.svg.view.SvgSkikoView.onRender)
    open val screenBounds: DoubleRectangle
        get() = ctm.transform(localBounds)

    open fun render(ctx: Context2d) {}

    open fun repr(): String? {
        return ", ctm: ${ctm.repr()}, $screenBounds"
    }
}

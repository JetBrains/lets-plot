/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.scene

import org.jetbrains.letsPlot.commons.geometry.AffineTransform
import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.Path2d
import org.jetbrains.letsPlot.raster.mapping.svg.SvgCanvasPeer
import kotlin.reflect.KProperty

internal abstract class Node : AttributeObject() { // Version counters
    override val attributeRegistry: AttributeRegistry get() = ATTRIBUTE_REGISTRY
    override var parent: Container? by variableAttr(null)

    var id: String? = null
    var isDirty: Boolean = true
    var href: String? = null
    var isMouseTransparent: Boolean = true

    var transform: AffineTransform by variableAttr(AffineTransform.IDENTITY)
    var styleClass: List<String>? by variableAttr(null)
    var bufferedRendering: Boolean by variableAttr(false)
    var isVisible: Boolean by variableAttr(true)
    var opacity: Float? by variableAttr(null)
    var clipPath: Path2d? by variableAttr(null)
    var peer: SvgCanvasPeer? by variableAttr(null)
    val bBoxLocal: DoubleRectangle by derivedAttr(::calculateLocalBBox)
    val ctm: AffineTransform by resolvedAttr { parentCtm ->
        (parentCtm ?: AffineTransform.IDENTITY).concat(transform)
    }

    val bBoxGlobal: DoubleRectangle get() = ctm.transform(bBoxLocal)

    fun detach() {
        onDetach()
    }

    open fun render(ctx: Context2d) {}

    protected open fun onDetach() {}

    protected open fun onAttributeChanged(attrSpec: AttributeSpec) { }

    protected abstract fun calculateLocalBBox(): DoubleRectangle

    internal fun <TValue> inheritValue(attrSpec: AttributeSpec, value: TValue) {
        attributes.inheritValue(this, attrSpec, value)
    }

    internal fun markDirty() {
        isDirty = true
        parent?.markDirty()
    }

    override fun onAttributeChangedInternal(attrSpec: AttributeSpec, oldValue: Any?, newValue: Any?) {
        if (attributeRegistry.getMetaData<Boolean>(attrSpec, AFFECTS_BBOX) == true) {
            invalidateGeometry()
            markDirty()
        }

        onAttributeChanged(attrSpec)
    }

    internal fun invalidateGeometry() {
        attributes.invalidateDerivedAttribute(BBoxLocalAttrSpec)
        parent?.invalidateGeometry()
    }

    open fun repr(): String? {
        return ", ctm: ${ctm.repr()}, $bBoxGlobal"
    }

    override fun toString(): String {
        val idStr = id?.let { "id: '$it' " } ?: ""
        return "class: ${this::class.simpleName}, $idStr${repr()}"
    }

    companion object {
        val ATTRIBUTE_REGISTRY = AttributeRegistry()

        val CLASS = ATTRIBUTE_REGISTRY.addClass(Node::class)

        val ParentAttrSpec = CLASS.registerVariableAttr(Node::parent, affectsBBox = true)
        val TransformAttrSpec = CLASS.registerVariableAttr(Node::transform, affectsBBox = true)
        val StyleClassAttrSpec = CLASS.registerVariableAttr(Node::styleClass)
        val BufferedRenderingAttrSpec = CLASS.registerVariableAttr(Node::bufferedRendering)
        val IsVisibleAttrSpec = CLASS.registerVariableAttr(Node::isVisible)
        val OpacityAttrSpec = CLASS.registerVariableAttr(Node::opacity)
        val ClipPathAttrSpec = CLASS.registerVariableAttr(Node::clipPath)
        val PeerAttrSpec = CLASS.registerVariableAttr(Node::peer, affectsBBox = true)
        val BBoxLocalAttrSpec = CLASS.registerDerivedAttr(Node::bBoxLocal, emptySet())
        val CtmAttrSpec = CLASS.registerResolvedAttr(Node::ctm, listOf(TransformAttrSpec, ParentAttrSpec))

        private const val AFFECTS_BBOX = "affectsBBox"
        private val EMPTY_MAP = emptyMap<String, Any>()

        fun AttributeRegistry.ClassMeta.registerVariableAttr(
            kProp: KProperty<*>,
            affectsBBox: Boolean = false
        ): VariableAttributeSpec<Any?> {
            val metaData = if (affectsBBox) mapOf(AFFECTS_BBOX to true) else EMPTY_MAP
            return registerVariableAttr(kProp, metaData)
        }
    }
}
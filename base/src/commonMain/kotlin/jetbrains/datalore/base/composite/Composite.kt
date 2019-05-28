package jetbrains.datalore.base.composite

/**
 * Generic composite structure. Examples of such structure:
 * - component tree in UI framework
 * - XML parse tree
 * - AST
 */
interface Composite<CompositeT : Composite<CompositeT>> : HasParent<CompositeT> {
    fun children(): MutableList<CompositeT>
}
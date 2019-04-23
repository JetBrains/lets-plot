package jetbrains.datalore.base.composite

interface HasParent<ParentT> {
    val parent: ParentT?
}
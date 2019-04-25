package jetbrains.datalore.base.composite

interface HasParent<ParentT : HasParent<ParentT>> {
    val parent: ParentT?
}
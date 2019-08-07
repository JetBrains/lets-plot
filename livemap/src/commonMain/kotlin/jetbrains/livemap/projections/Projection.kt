package jetbrains.livemap.projections

interface Projection<T> {
    fun project(v: T): T
    fun invert(v: T): T
}
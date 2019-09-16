package jetbrains.livemap.projections

interface Transform<InT, OutT> {
    fun project(v: InT): OutT
    fun invert(v: OutT): InT
    //operator fun invoke(v: InT): OutT = project(v)
    //operator fun invoke(v: OutT): InT = invert(v)
}

interface Projection<T> : Transform<T, T> {
}

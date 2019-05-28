package jetbrains.datalore.mapper.core

/**
 * Synchronizer is a reusable part of [Mapper]
 */
interface Synchronizer {

    companion object {
        val EMPTY_ARRAY = arrayOfNulls<Synchronizer>(0)
    }

    fun attach(ctx: SynchronizerContext)
    fun detach()
}
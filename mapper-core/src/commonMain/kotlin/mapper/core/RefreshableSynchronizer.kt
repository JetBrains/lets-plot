package jetbrains.datalore.mapper.core


/**
 * A simple kind of synchronizer which doesn't listen to a model and refreshes its part of output only when
 * the refresh() method is explicitly called.
 */
interface RefreshableSynchronizer : Synchronizer {
    fun refresh()
}
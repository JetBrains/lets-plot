package jetbrains.livemap.entities.regions

import jetbrains.datalore.base.projectionGeometry.Generic
import jetbrains.datalore.base.projectionGeometry.MultiPolygon
import jetbrains.datalore.base.projectionGeometry.QuadKey
import jetbrains.gis.geoprotocol.Geometry
import jetbrains.livemap.containers.LruCache
import jetbrains.livemap.core.ecs.EcsComponent
import jetbrains.livemap.core.ecs.EcsEntity

object Components {

    class CachedFragmentsComponent : EcsComponent {
        private val myCache = HashMap<FragmentKey, EcsEntity>()

        internal operator fun contains(fragmentKey: FragmentKey) = myCache.containsKey(fragmentKey)

        fun keys() = myCache.keys

        fun store(fragmentKey: FragmentKey, fragmentEntity: EcsEntity) {
            if (myCache.containsKey(fragmentKey)) {
                error("Already existing fragment: ${fragmentEntity.name}")
            }

            myCache[fragmentKey] = fragmentEntity
        }

        operator fun get(fragmentKey: FragmentKey): EcsEntity? {
            return myCache[fragmentKey]
        }

        fun dispose(fragment: FragmentKey) {
            get(fragment)?.remove()
            myCache.remove(fragment)
        }
    }

    class EmptyFragmentsComponent : EcsComponent {

        private val myCache: LruCache<String, LruCache<QuadKey, Boolean>> = LruCache(REGIONS_CACHE_LIMIT)

        fun createCache(): LruCache<QuadKey, Boolean> {
            return LruCache(EMPTY_FRAGMENTS_CACHE_LIMIT)
        }

        internal fun add(fragmentKey: FragmentKey) {
            myCache.getOrPut(fragmentKey.regionId, ::createCache).put(fragmentKey.quadKey, true)
        }

        internal fun contains(regionId: String, quadKey: QuadKey): Boolean {
            val emptyQuads = myCache[regionId]
            return emptyQuads != null && emptyQuads.containsKey(quadKey)
        }

        fun addAll(emptyFragments: Set<FragmentKey>) {
            for (emptyFragment in emptyFragments) {
                add(emptyFragment)
            }
        }

        companion object {
            private const val EMPTY_FRAGMENTS_CACHE_LIMIT = 50000 // they are pretty cheap but have large impact on performance
            private const val REGIONS_CACHE_LIMIT = 5000 // safety net for dynamic regions (if ever)
        }
    }

    class ChangedFragmentsComponent : EcsComponent {
        private val myNewFragments = HashSet<FragmentKey>()
        private val myObsoleteFragments = HashSet<FragmentKey>()

        val requested: Set<FragmentKey>
            get() = myNewFragments

        val obsolete: Set<FragmentKey>
            get() = myObsoleteFragments

        fun setToAdd(keys: Collection<FragmentKey>) {
            myNewFragments.clear()
            myNewFragments.addAll(keys)
        }

        fun setToRemove(keys: Collection<FragmentKey>) {
            myObsoleteFragments.clear()
            myObsoleteFragments.addAll(keys)
        }

        fun anyChanges(): Boolean {
            return !myNewFragments.isEmpty() && myObsoleteFragments.isEmpty()
        }
    }

    class DownloadingFragmentsComponent : EcsComponent {
        val queue = HashMap<Int, MutableSet<FragmentKey>>()
        val downloading = HashSet<FragmentKey>()
        var downloaded: MutableMap<FragmentKey, MultiPolygon<Generic>> = HashMap()
            set(fragments) {
                downloaded.clear()
                downloaded.putAll(fragments)
            }

        fun getZoomQueue(zoom: Int): Set<FragmentKey> {
            return queue.getOrElse(zoom, { emptySet() })
        }

        fun extendQueue(keys: Set<FragmentKey>) {
            for (key in keys) {
                queue.getOrPut(key.zoom(), ::HashSet).add(key)
            }
        }

        fun reduceQueue(keys: Set<FragmentKey>) {
            for (key in keys) {
                queue[key.quadKey.zoom()]?.remove(key)
            }
        }

        internal fun extendDownloading(keys: Set<FragmentKey>) {
            downloading.addAll(keys)
        }

        internal fun reduceDownloading(keys: Set<FragmentKey>) {
            downloading.removeAll(keys)
        }
    }

    class FragmentComponent(val fragmentKey: FragmentKey) : EcsComponent {
        val quad: QuadKey get() = fragmentKey.quadKey
    }

    class RegionComponent : EcsComponent {

        var id: String? = null
        private val myFragmentEntities = HashSet<EcsEntity>()

        var fragments: Collection<EcsEntity>
            get() = myFragmentEntities
            set(fragments) {
                myFragmentEntities.clear()
                myFragmentEntities.addAll(fragments)
            }

        internal fun addFragment(fragmentEntity: EcsEntity) {
            myFragmentEntities.add(fragmentEntity)
        }

        internal fun removeFragment(fragmentEntityId: EcsEntity) {
            myFragmentEntities.remove(fragmentEntityId)
        }
    }

//    object FragmentEntityView {
//
//        fun getFragmentKey(fragmentEntity: EcsEntity): FragmentKey {
//            return getFragment(fragmentEntity).fragmentKey
//        }
//
//        fun getParentLayer(fragmentEntity: EcsEntity): ParentLayerComponent {
//            return ParentLayerComponent[fragmentEntity]
//        }
//
//        fun getFragment(fragmentEntity: EcsEntity): FragmentComponent {
//            return FragmentComponent[fragmentEntity]
//        }
//
//        fun getWorldDimension(fragmentEntity: EcsEntity): WorldDimensionComponent {
//            return WorldDimensionComponent.get(fragmentEntity)
//        }
//
//        fun getWorldOrigin(fragmentEntity: EcsEntity): WorldOriginComponent {
//            return WorldOriginComponent.get(fragmentEntity)
//        }
//
//        fun getFragmentGeometry(fragmentEntity: EcsEntity): FragmentGeometryComponent {
//            return FragmentGeometryComponent[fragmentEntity]
//        }
//
//        fun getScale(fragmentEntity: EcsEntity): ScaleComponent {
//            return ScaleComponent.get(fragmentEntity)
//        }
//
//        fun tryGetScreenGeometry(fragmentEntity: EcsEntity): ScreenGeometryComponent? {
//            return if (fragmentEntity.contains(ScreenGeometryComponent::class)) {
//                ScreenGeometryComponent.get(fragmentEntity)
//            } else {
//                null
//            }
//        }
//
//        fun tryGetScreenOrigin(fragmentEntity: EcsEntity): ScreenOriginComponent? {
//            return if (fragmentEntity.contains(ScreenOriginComponent::class)) {
//                ScreenOriginComponent.get(fragmentEntity)
//            } else {
//                null
//            }
//        }
//
//        fun tryGetScreenLoop(fragmentEntity: EcsEntity): ScreenLoopComponent? {
//            return if (fragmentEntity.contains(ScreenOriginComponent::class)) {
//                ScreenLoopComponent.get(fragmentEntity)
//            } else {
//                null
//            }
//        }
//    }
//
//    object RegionEntityView {
//        val COMPONENTS: List<Class<out EcsComponent>> = asList<T>(
//            RegionComponent::class,
//            RendererComponent::class,
//            StyleComponent::class,
//            ParentLayerComponent::class
//        )
//
//        fun getRegionComponent(regionEntity: EcsEntity): RegionComponent {
//            return RegionComponent[regionEntity]
//        }
//
//        fun getRenderer(regionEntity: EcsEntity): RendererComponent {
//            return RendererComponent.get(regionEntity)
//        }
//
//        fun getStyleComponent(regionEntity: EcsEntity): StyleComponent {
//            return StyleComponent.get(regionEntity)
//        }
//
//        fun getParentLayerComponent(regionEntity: EcsEntity): ParentLayerComponent {
//            return ParentLayerComponent[regionEntity]
//        }
//    }

    class EmittedFragmentsComponent : EcsComponent {
        private val myEmitted = HashSet<FragmentKey>()

        fun setEmitted(fragments: Set<FragmentKey>): EmittedFragmentsComponent {
            myEmitted.clear()
            myEmitted.addAll(fragments)
            return this
        }

        internal fun keys(): Set<FragmentKey> {
            return myEmitted
        }
    }

    class EmittedRegionsComponent : EcsComponent {
        private val myEmitted = HashSet<String>()

        fun keys(): Set<String> {
            return myEmitted
        }
    }

    /**
     * Fragment is counted as streaming when it is downloading or projecting.
     */
    class StreamingFragmentsComponent : EcsComponent {
        private val fetching = HashMap<FragmentKey, EcsEntity?>()

        fun keys(): Set<FragmentKey> {
            return fetching.keys
        }

        internal fun add(key: FragmentKey) {
            fetching[key] = null
        }

        internal fun addAll(keys: Set<FragmentKey>) {
            keys.forEach { key -> fetching[key] = null }
        }

        internal operator fun set(key: FragmentKey, entity: EcsEntity) {
            fetching.put(key, entity)
        }

        internal fun getEntity(fragmentKey: FragmentKey): EcsEntity? {
            return fetching[fragmentKey]
        }

        internal fun remove(key: FragmentKey) {
            fetching.remove(key)
        }
    }

    class FragmentGeometryComponent : EcsComponent {
        var geometry: Geometry? = null
    }
}
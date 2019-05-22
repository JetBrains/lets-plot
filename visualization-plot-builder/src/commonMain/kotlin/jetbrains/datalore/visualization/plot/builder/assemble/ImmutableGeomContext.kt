package jetbrains.datalore.visualization.plot.builder.assemble

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.GeomContext
import jetbrains.datalore.visualization.plot.base.event3.GeomTargetCollector

interface ImmutableGeomContext : GeomContext {

    fun with(): Builder

    interface Builder {
        fun aesthetics(aesthetics: Aesthetics?): Builder

        fun aestheticMappers(aestheticMappers: Map<Aes<*>, (Double?) -> Any?>?): Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): Builder

        fun build(): ImmutableGeomContext
    }
}

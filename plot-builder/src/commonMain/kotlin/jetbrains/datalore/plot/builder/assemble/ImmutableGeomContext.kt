package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.GeomContext
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector

interface ImmutableGeomContext : GeomContext {

    fun with(): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

    interface Builder {
        fun aesthetics(aesthetics: Aesthetics?): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

        fun aestheticMappers(aestheticMappers: Map<Aes<*>, (Double?) -> Any?>?): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

        fun geomTargetCollector(geomTargetCollector: GeomTargetCollector): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext.Builder

        fun build(): jetbrains.datalore.plot.builder.assemble.ImmutableGeomContext
    }
}

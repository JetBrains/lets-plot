package jetbrains.datalore.visualization.plot.gog.core.render.geom

import jetbrains.datalore.visualization.plot.gog.core.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.gog.core.render.Aesthetics
import jetbrains.datalore.visualization.plot.gog.core.render.Geom
import jetbrains.datalore.visualization.plot.gog.plot.GeomKind

class LivemapLayerData(val geom: Geom, val geomKind: GeomKind, val aesthetics: Aesthetics, val dataAccess: MappedDataAccess)

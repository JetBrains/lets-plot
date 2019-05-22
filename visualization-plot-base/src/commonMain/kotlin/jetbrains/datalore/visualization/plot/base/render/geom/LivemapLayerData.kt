package jetbrains.datalore.visualization.plot.base.render.geom

import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess
import jetbrains.datalore.visualization.plot.base.render.Geom

class LivemapLayerData(val geom: Geom, val geomKind: GeomKind, val aesthetics: Aesthetics, val dataAccess: MappedDataAccess)

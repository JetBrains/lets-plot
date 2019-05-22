package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.visualization.plot.base.Aesthetics
import jetbrains.datalore.visualization.plot.base.Geom
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.event.MappedDataAccess

class LivemapLayerData(val geom: Geom, val geomKind: GeomKind, val aesthetics: Aesthetics, val dataAccess: MappedDataAccess)

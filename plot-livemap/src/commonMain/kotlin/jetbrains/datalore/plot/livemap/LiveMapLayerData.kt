package jetbrains.datalore.plot.livemap

import jetbrains.datalore.plot.base.Aesthetics
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.MappedDataAccess

class LiveMapLayerData(
    val geom: Geom,
    val geomKind: GeomKind,
    val aesthetics: Aesthetics,
    val dataAccess: MappedDataAccess
)

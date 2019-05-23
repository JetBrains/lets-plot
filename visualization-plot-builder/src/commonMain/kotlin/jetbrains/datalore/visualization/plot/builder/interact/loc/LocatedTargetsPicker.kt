package jetbrains.datalore.visualization.plot.builder.interact.loc

import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetLocator.LocatedTargets

internal class LocatedTargetsPicker {

    private val myPicked = ArrayList<LocatedTargets>()
    private var myMinDistance = 0.0

    val picked: List<LocatedTargets>
        get() = myPicked

    fun addLocatedTargets(locatedTargets: LocatedTargets) {
        val distance = distance(locatedTargets)
        if (distance > CUTOFF_DISTANCE) {
            return
        }

        if (myPicked.isEmpty() || myMinDistance > distance) {
            myPicked.clear()
            myPicked.add(locatedTargets)
            myMinDistance = distance
        } else if (myMinDistance == distance && sameGeomKind(myPicked[0], locatedTargets)) {
            myPicked.add(locatedTargets)
        }
    }

    companion object {
        internal const val CUTOFF_DISTANCE = 30.0
        internal const val FAKE_DISTANCE = 15.0
        private val UNIVARIATE_GEOMS = listOf(
                GeomKind.DENSITY,
                GeomKind.FREQPOLY,
                GeomKind.BOX_PLOT,
                GeomKind.HISTOGRAM,
                GeomKind.LINE,
                GeomKind.AREA,
                GeomKind.BAR,
                GeomKind.ERROR_BAR
        )

        private fun distance(locatedTargetList: LocatedTargets): Double {
            val distance = locatedTargetList.distance
            // Special case for geoms like histogram, when mouse inside a rect or only X projection is used (so a distance
            // between cursor is zero). Fake the distance to give a chance for tooltips from other layers.
            return if (distance == 0.0) {
                FAKE_DISTANCE
            } else distance
        }

        private fun sameGeomKind(lft: LocatedTargets, rgt: LocatedTargets): Boolean {
            return lft.geomKind === rgt.geomKind && UNIVARIATE_GEOMS.contains(rgt.geomKind)
        }
    }
}

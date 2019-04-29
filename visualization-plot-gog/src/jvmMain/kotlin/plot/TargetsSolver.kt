package jetbrains.datalore.visualization.plot.gog.plot

import jetbrains.datalore.visualization.plot.core.GeomKind
import jetbrains.datalore.visualization.plot.gog.core.event3.GeomTargetLocator.LocatedTargets

internal class TargetsSolver {

    private val myLocatedTargetsList = ArrayList<LocatedTargets>()
    private var myMinDistance = 0.0

    fun addLocatedTargets(locatedTargets: LocatedTargets?) {
        if (locatedTargets == null) {
            return
        }

        val distance = distance(locatedTargets)
        if (distance > CUTOFF_DISTANCE) {
            return
        }

        if (myLocatedTargetsList.isEmpty() || myMinDistance > distance) {
            myLocatedTargetsList.clear()
            myLocatedTargetsList.add(locatedTargets)
            myMinDistance = distance
        } else if (myMinDistance == distance && sameGeomKind(myLocatedTargetsList[0], locatedTargets)) {
            myLocatedTargetsList.add(locatedTargets)
        }
    }

    fun solve(): List<LocatedTargets> {
        return myLocatedTargetsList
    }

    companion object {
        val CUTOFF_DISTANCE = 30.0
        val FAKE_DISTANCE = 15.0
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

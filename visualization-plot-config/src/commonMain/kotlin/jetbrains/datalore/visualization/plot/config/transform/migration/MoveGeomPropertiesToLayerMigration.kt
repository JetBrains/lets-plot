package jetbrains.datalore.visualization.plot.config.transform.migration

import jetbrains.datalore.visualization.plot.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.config.Option.Plot
import jetbrains.datalore.visualization.plot.config.transform.PlotSpecTransformUtil
import jetbrains.datalore.visualization.plot.config.transform.SpecChange
import jetbrains.datalore.visualization.plot.config.transform.SpecChangeContext
import jetbrains.datalore.visualization.plot.config.transform.SpecSelector

/**
 * Migrate from old schema:
 * <pre>
 * layers:  [
 * {
 * geom: {
 * name: 'point'
 * data: ....
 * }
 * },
 * ...
 * ]
</pre> *
 *
 *
 * to new schema:
 *
 *
 * <pre>
 * layers:  [
 * {
 * geom: 'point'
 * data: ....
 * },
 * ...
 * ]
</pre> *
 *
 *
 *
 *
 * ToDo: add test
 */
class MoveGeomPropertiesToLayerMigration : SpecChange {

    override fun isApplicable(spec: Map<String, Any>): Boolean {
        // migrate if value of 'geom' property is Map-object
        return spec[GEOM] is Map<*, *>
    }

    override fun apply(spec: MutableMap<String, Any>, ctx: SpecChangeContext) {
        val geomSpec = spec.remove(GEOM) as MutableMap<*, *>
        val name = geomSpec.remove("name") as String

        spec[GEOM] = name
        val geomSpec1 = geomSpec as Map<String, Any>
        spec.putAll(geomSpec1)
    }

    companion object {
        fun specSelector(isGGBunch: Boolean): SpecSelector {
            val parts = ArrayList<String>()
            if (isGGBunch) {
                parts.addAll(PlotSpecTransformUtil.GGBUNCH_KEY_PARTS.toList())
            }
            parts.add(Plot.LAYERS)
            return SpecSelector.from(parts)  // apply to each element in 'layers' list (i.e. to layer spec)
        }
    }
}

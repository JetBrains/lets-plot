package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.visualization.plot.gog.core.render.point.NamedShape
import jetbrains.datalore.visualization.plot.gog.core.render.point.PointShape
import jetbrains.datalore.visualization.plot.gog.core.render.point.PointShapes

object ShapeMapper {
    val NA_VALUE = PointShapes.dot()

    fun allShapes(): List<PointShape> {
        val bestSix: List<PointShape> = listOf(
                NamedShape.SOLID_CIRCLE,
                NamedShape.SOLID_TRIANGLE_UP,
                NamedShape.SOLID_SQUARE,
                NamedShape.STICK_PLUS,
                NamedShape.STICK_SQUARE_CROSS,
                NamedShape.STICK_STAR
        )

        val theRest = LinkedHashSet<PointShape>(listOf(*NamedShape.values()))
        theRest.removeAll(bestSix)

        val shapes = ArrayList(bestSix)
        shapes.addAll(theRest)
        return shapes
    }

    /**
     * see: scale_shape(..., solid = FALSE)
     */
    fun hollowShapes(): List<PointShape> {
        val bestThreeHollow = listOf(
                NamedShape.STICK_CIRCLE,
                NamedShape.STICK_TRIANGLE_UP,
                NamedShape.STICK_SQUARE
        )

        val theRest = LinkedHashSet(listOf(*NamedShape.values()))
        theRest.removeAll(bestThreeHollow)

        val shapes = ArrayList(bestThreeHollow)
        for (shape in theRest) {
            if (shape.isHollow) {
                shapes.add(shape)
            }
        }
        return shapes
    }
}

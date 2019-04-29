package jetbrains.datalore.visualization.plot.gog.plot.scale.mapper

import jetbrains.datalore.visualization.plot.gog.core.render.linetype.LineType
import jetbrains.datalore.visualization.plot.gog.core.render.linetype.NamedLineType
import java.util.*

object LineTypeMapper {
    val NA_VALUE: LineType = NamedLineType.SOLID

    fun allLineTypes(): List<LineType> {
        return Arrays.asList<LineType>(
                NamedLineType.SOLID,
                NamedLineType.DASHED,
                NamedLineType.DOTTED,
                NamedLineType.DOTDASH,
                NamedLineType.LONGDASH,
                NamedLineType.TWODASH
        )
    }
}

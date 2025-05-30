/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.data

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Variable.Source.TRANSFORM
import org.jetbrains.letsPlot.core.plot.base.aes.AesVisitor

object TransformVar {
    val X = DataFrame.Variable("transform.X", TRANSFORM)
    val Y = DataFrame.Variable("transform.Y", TRANSFORM)
    val Z = DataFrame.Variable("transform.Z", TRANSFORM)
    val YMIN = DataFrame.Variable("transform.YMIN", TRANSFORM)
    val YMAX = DataFrame.Variable("transform.YMAX", TRANSFORM)
    val COLOR = DataFrame.Variable("transform.COLOR", TRANSFORM)
    val FILL = DataFrame.Variable("transform.FILL", TRANSFORM)
    val PAINT_A = DataFrame.Variable("transform.PAINT_A", TRANSFORM)
    val PAINT_B = DataFrame.Variable("transform.PAINT_B", TRANSFORM)
    val PAINT_C = DataFrame.Variable("transform.PAINT_C", TRANSFORM)
    val ALPHA = DataFrame.Variable("transform.ALPHA", TRANSFORM)
    val SHAPE = DataFrame.Variable("transform.SHAPE", TRANSFORM)
    val LINETYPE = DataFrame.Variable("transform.LINETYPE", TRANSFORM)
    val SIZE = DataFrame.Variable("transform.SIZE", TRANSFORM)
    val STROKE = DataFrame.Variable("transform.STROKE", TRANSFORM)
    val LINEWIDTH = DataFrame.Variable("transform.LINEWIDTH", TRANSFORM)
    val STACKSIZE = DataFrame.Variable("transform.STACKSIZE", TRANSFORM)
    val WIDTH = DataFrame.Variable("transform.WIDTH", TRANSFORM)
    val HEIGHT = DataFrame.Variable("transform.HEIGHT", TRANSFORM)
    val BINWIDTH = DataFrame.Variable("transform.BINWIDTH", TRANSFORM)
    val VIOLINWIDTH = DataFrame.Variable("transform.VIOLINWIDTH", TRANSFORM)
    val WEIGHT = DataFrame.Variable("transform.WEIGHT", TRANSFORM)
    val INTERCEPT = DataFrame.Variable("transform.INTERCEPT", TRANSFORM)
    val SLOPE = DataFrame.Variable("transform.SLOPE", TRANSFORM)
    val XINTERCEPT = DataFrame.Variable("transform.XINTERCEPT", TRANSFORM)
    val YINTERCEPT = DataFrame.Variable("transform.YINTERCEPT", TRANSFORM)
    val LOWER = DataFrame.Variable("transform.LOWER", TRANSFORM)
    val MIDDLE = DataFrame.Variable("transform.MIDDLE", TRANSFORM)
    val UPPER = DataFrame.Variable("transform.UPPER", TRANSFORM)
    val XLOWER = DataFrame.Variable("transform.XLOWER", TRANSFORM)
    val XMIDDLE = DataFrame.Variable("transform.XMIDDLE", TRANSFORM)
    val XUPPER = DataFrame.Variable("transform.XUPPER", TRANSFORM)
    val SAMPLE = DataFrame.Variable("transform.SAMPLE", TRANSFORM)
    val QUANTILE = DataFrame.Variable("transform.QUANTILE", TRANSFORM)
    val MAP_ID = DataFrame.Variable("transform.MAP_ID", TRANSFORM)
    val FRAME = DataFrame.Variable("transform.FRAME", TRANSFORM)
    val SPEED = DataFrame.Variable("transform.SPEED", TRANSFORM)
    val FLOW = DataFrame.Variable("transform.FLOW", TRANSFORM)
    val XMIN = DataFrame.Variable("transform.XMIN", TRANSFORM)
    val XMAX = DataFrame.Variable("transform.XMAX", TRANSFORM)
    val XEND = DataFrame.Variable("transform.XEND", TRANSFORM)
    val YEND = DataFrame.Variable("transform.YEND", TRANSFORM)
    val LABEL = DataFrame.Variable("transform.LABEL", TRANSFORM)
    val FONT_FAMILY = DataFrame.Variable("transform.FONT_FAMILY", TRANSFORM)
    val FONT_FACE = DataFrame.Variable("transform.FONT_FACE", TRANSFORM)
    val LINEHEIGHT = DataFrame.Variable("transform.LINEHEIGHT", TRANSFORM)
    val HJUST = DataFrame.Variable("transform.HJUST", TRANSFORM)
    val VJUST = DataFrame.Variable("transform.VJUST", TRANSFORM)
    val ANGLE = DataFrame.Variable("transform.ANGLE", TRANSFORM)
    val RADIUS = DataFrame.Variable("transform.RADIUS", TRANSFORM)
    val SLICE = DataFrame.Variable("transform.SLICE", TRANSFORM)
    val EXPLODE = DataFrame.Variable("transform.EXPLODE", TRANSFORM)
    val SIZE_START = DataFrame.Variable("transform.SIZE_START", TRANSFORM)
    val SIZE_END = DataFrame.Variable("transform.SIZE_END", TRANSFORM)
    val STROKE_START = DataFrame.Variable("transform.STROKE_START", TRANSFORM)
    val STROKE_END = DataFrame.Variable("transform.STROKE_END", TRANSFORM)
    val POINT_SIZE = DataFrame.Variable("transform.POINT_SIZE", TRANSFORM)
    val SEGMENT_COLOR = DataFrame.Variable("transform.SEGMENT_COLOR", TRANSFORM)
    val SEGMENT_SIZE = DataFrame.Variable("transform.SEGMENT_SIZE", TRANSFORM)
    val SEGMENT_ALPHA = DataFrame.Variable("transform.SEGMENT_ALPHA", TRANSFORM)

    private val VAR_BY_AES = TransformVarByAes()
    private val VAR_BY_NAME: Map<String, DataFrame.Variable>
    private val AES_BY_VAR: Map<DataFrame.Variable, Aes<*>>

    init {
        VAR_BY_NAME = Aes.values().associate { aes ->
            val variable = VAR_BY_AES.visit(aes)
            variable.name to variable
        }

        AES_BY_VAR = Aes.values().associateBy { aes ->
            VAR_BY_AES.visit(aes)
        }
    }

    fun isTransformVar(varName: String): Boolean {
        return VAR_BY_NAME.containsKey(varName)
    }

    operator fun get(varName: String): DataFrame.Variable {
        check(VAR_BY_NAME.containsKey(varName)) { "Unknown transform variable $varName" }
        return VAR_BY_NAME[varName]!!
    }

    fun forAes(aes: Aes<*>): DataFrame.Variable {
        return VAR_BY_AES.visit(aes)
    }

    fun toAes(variable: DataFrame.Variable): Aes<*> {
        return AES_BY_VAR.getValue(variable)
    }

    private class TransformVarByAes : AesVisitor<DataFrame.Variable>() {

        override fun x(): DataFrame.Variable {
            return X
        }

        override fun y(): DataFrame.Variable {
            return Y
        }

        override fun z(): DataFrame.Variable {
            return Z
        }

        override fun ymin(): DataFrame.Variable {
            return YMIN
        }

        override fun ymax(): DataFrame.Variable {
            return YMAX
        }

        override fun color(): DataFrame.Variable {
            return COLOR
        }

        override fun fill(): DataFrame.Variable {
            return FILL
        }

        override fun paint_a(): DataFrame.Variable {
            return PAINT_A
        }

        override fun paint_b(): DataFrame.Variable {
            return PAINT_B
        }

        override fun paint_c(): DataFrame.Variable {
            return PAINT_C
        }

        override fun alpha(): DataFrame.Variable {
            return ALPHA
        }

        override fun shape(): DataFrame.Variable {
            return SHAPE
        }

        override fun lineType(): DataFrame.Variable {
            return LINETYPE
        }

        override fun size(): DataFrame.Variable {
            return SIZE
        }

        override fun stroke(): DataFrame.Variable {
            return STROKE
        }

        override fun linewidth(): DataFrame.Variable {
            return LINEWIDTH
        }

        override fun stacksize(): DataFrame.Variable {
            return STACKSIZE
        }

        override fun width(): DataFrame.Variable {
            return WIDTH
        }

        override fun height(): DataFrame.Variable {
            return HEIGHT
        }

        override fun binwidth(): DataFrame.Variable {
            return BINWIDTH
        }

        override fun violinwidth(): DataFrame.Variable {
            return VIOLINWIDTH
        }

        override fun weight(): DataFrame.Variable {
            return WEIGHT
        }

        override fun intercept(): DataFrame.Variable {
            return INTERCEPT
        }

        override fun slope(): DataFrame.Variable {
            return SLOPE
        }

        override fun interceptX(): DataFrame.Variable {
            return XINTERCEPT
        }

        override fun interceptY(): DataFrame.Variable {
            return YINTERCEPT
        }

        override fun lower(): DataFrame.Variable {
            return LOWER
        }

        override fun middle(): DataFrame.Variable {
            return MIDDLE
        }

        override fun upper(): DataFrame.Variable {
            return UPPER
        }

        override fun xlower(): DataFrame.Variable {
            return XLOWER
        }

        override fun xmiddle(): DataFrame.Variable {
            return XMIDDLE
        }

        override fun xupper(): DataFrame.Variable {
            return XUPPER
        }

        override fun sample(): DataFrame.Variable {
            return SAMPLE
        }

        override fun quantile(): DataFrame.Variable {
            return QUANTILE
        }

        override fun mapId(): DataFrame.Variable {
            return MAP_ID
        }

        override fun frame(): DataFrame.Variable {
            return FRAME
        }

        override fun speed(): DataFrame.Variable {
            return SPEED
        }

        override fun flow(): DataFrame.Variable {
            return FLOW
        }

        override fun xmin(): DataFrame.Variable {
            return XMIN
        }

        override fun xmax(): DataFrame.Variable {
            return XMAX
        }

        override fun xend(): DataFrame.Variable {
            return XEND
        }

        override fun yend(): DataFrame.Variable {
            return YEND
        }

        override fun label(): DataFrame.Variable {
            return LABEL
        }

        override fun family(): DataFrame.Variable {
            return FONT_FAMILY
        }

        override fun fontface(): DataFrame.Variable {
            return FONT_FACE
        }

        override fun lineheight(): DataFrame.Variable {
            return LINEHEIGHT
        }

        override fun hjust(): DataFrame.Variable {
            return HJUST
        }

        override fun vjust(): DataFrame.Variable {
            return VJUST
        }

        override fun angle(): DataFrame.Variable {
            return ANGLE
        }

        override fun radius(): DataFrame.Variable {
            return RADIUS
        }

        override fun slice(): DataFrame.Variable {
            return SLICE
        }

        override fun explode(): DataFrame.Variable {
            return EXPLODE
        }

        override fun sizeStart(): DataFrame.Variable {
            return SIZE_START
        }

        override fun sizeEnd(): DataFrame.Variable {
            return SIZE_END
        }

        override fun strokeStart(): DataFrame.Variable {
            return STROKE_START
        }

        override fun strokeEnd(): DataFrame.Variable {
            return STROKE_END
        }

        override fun pointSize(): DataFrame.Variable {
            return POINT_SIZE
        }

        override fun segmentColor(): DataFrame.Variable {
            return SEGMENT_COLOR
        }

        override fun segmentSize(): DataFrame.Variable {
            return SEGMENT_SIZE
        }

        override fun segmentAlpha(): DataFrame.Variable {
            return SEGMENT_ALPHA
        }
    }
}

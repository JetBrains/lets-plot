package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.canvas.Context2d
import jetbrains.datalore.visualization.base.svg.SvgPathData

internal class PathProcessor private constructor(private val myContext: Context2d) {

  private var myLastPoint = DoubleVector.ZERO
  private var myClosePoint = DoubleVector.ZERO
  private var myLastCurve: DoubleVector? = null
  private var myLastQCurve: DoubleVector? = null

  private fun applyPath(path: List<ParsingUtil.Result>) {
    for (r in path) {
      val name = r.name[0]
      val absolute = name == Character.toUpperCase(name)
      val action = SvgPathData.Action.get(name)

      if (action != SvgPathData.Action.VERTICAL_LINE_TO && action != SvgPathData.Action.HORIZONTAL_LINE_TO) {
        setLastPoint(absolute)
      }

      when (action) {
        SvgPathData.Action.MOVE_TO -> processMoveTo(r)
        SvgPathData.Action.LINE_TO -> processLineTo(r)
        SvgPathData.Action.HORIZONTAL_LINE_TO -> processHorizontalLineTo(absolute, r)
        SvgPathData.Action.VERTICAL_LINE_TO -> processVerticalLineTo(absolute, r)
        SvgPathData.Action.CLOSE_PATH -> closePath()
        SvgPathData.Action.CURVE_TO -> processCurveTo(r)
        SvgPathData.Action.SMOOTH_CURVE_TO -> processSmoothCurveTo(r)
        SvgPathData.Action.QUADRATIC_BEZIER_CURVE_TO -> processQuadraticCurveTo(r)
        SvgPathData.Action.SMOOTH_QUADRATIC_BEZIER_CURVE_TO -> processSmoothQuadraticCurveTo(r)
        SvgPathData.Action.ELLIPTICAL_ARC -> processEllipticalArc(r)
        else -> throw IllegalArgumentException("Unknown action in path: $name")
      }

      if (action != SvgPathData.Action.CURVE_TO && action != SvgPathData.Action.SMOOTH_CURVE_TO) {
        resetLastCurve()
      }

      if (action != SvgPathData.Action.QUADRATIC_BEZIER_CURVE_TO && action != SvgPathData.Action.SMOOTH_QUADRATIC_BEZIER_CURVE_TO) {
        resetLastQCurve()
      }
    }
  }

  private fun setLastPoint(absolute: Boolean) {
    myLastPoint = if (absolute) DoubleVector.ZERO else myLastPoint
  }

  private fun resetLastCurve() {
    myLastCurve = null
  }

  private fun resetLastQCurve() {
    myLastQCurve = null
  }

  private fun closePath() {
    myContext.lineTo(myClosePoint.x, myClosePoint.y)
    myLastPoint = myClosePoint
  }

  private fun processMoveTo(r: ParsingUtil.Result) {
    val p = myLastPoint.add(r.getVector(MOVE_TO_P))
    myContext.moveTo(p.x, p.y)
    myLastPoint = p
    myClosePoint = p
  }

  private fun processLineTo(r: ParsingUtil.Result) {
    val p = myLastPoint.add(r.getVector(LINE_TO_P))
    myContext.lineTo(p.x, p.y)
    myLastPoint = p
  }

  private fun processHorizontalLineTo(absolute: Boolean, r: ParsingUtil.Result) {
    val x = (if (absolute) 0.0 else myLastPoint.x) + r.getParam(HORIZONTAL_LINE_TO_X)!!
    val p = DoubleVector(x, myLastPoint.y)
    myContext.lineTo(p.x, p.y)
    myLastPoint = p
  }

  private fun processVerticalLineTo(absolute: Boolean, r: ParsingUtil.Result) {
    val y = (if (absolute) 0.0 else myLastPoint.y) + r.getParam(VERTICAL_LINE_TO_Y)!!
    val p = DoubleVector(myLastPoint.x, y)
    myContext.lineTo(p.x, p.y)
    myLastPoint = p
  }

  private fun processCurveTo(r: ParsingUtil.Result) {
    val p1 = myLastPoint.add(r.getVector(CURVE_TO_P1))
    val p2 = myLastPoint.add(r.getVector(CURVE_TO_P2))
    val p = myLastPoint.add(r.getVector(CURVE_TO_P))
    myContext.bezierCurveTo(p1.x, p1.y, p2.x, p2.y, p.x, p.y)
    myLastPoint = p
    myLastCurve = p2
  }

  private fun processSmoothCurveTo(r: ParsingUtil.Result) {
    val p1 = myLastPoint.add(r.getVector(SMOOTH_CURVE_TO_P1))
    val p = myLastPoint.add(r.getVector(SMOOTH_CURVE_TO_P))
    val p2 = if (myLastCurve == null) myLastPoint else smoothCurveBasePoint(myLastCurve!!)
    myContext.bezierCurveTo(p2.x, p2.y, p1.x, p1.y, p.x, p.y)
    myLastCurve = p1
    myLastPoint = p
  }

  private fun processQuadraticCurveTo(r: ParsingUtil.Result) {
    val p1 = myLastPoint.add(r.getVector(QUADRATIC_BEZIER_CURVE_TO_P1))
    val p = myLastPoint.add(r.getVector(QUADRATIC_BEZIER_CURVE_TO_P))
    myContext.quadraticCurveTo(p1.x, p1.y, p.x, p.y)
    myLastPoint = p
    myLastCurve = p1
    myLastQCurve = p1
  }

  private fun processSmoothQuadraticCurveTo(r: ParsingUtil.Result) {
    val p = myLastPoint.add(r.getVector(SMOOTH_QUADRATIC_BEZIER_CURVE_TO_P))
    myLastPoint = p
    if (myLastQCurve == null) {
      myContext.lineTo(p.x, p.y)
    } else {
      val p1 = smoothCurveBasePoint(myLastQCurve!!)
      myContext.quadraticCurveTo(p1.x, p1.y, p.x, p.y)
    }
  }

  private fun processEllipticalArc(r: ParsingUtil.Result) {
    val p = myLastPoint.add(r.getVector(ELLIPTICAL_ARC_P))

    val converter = ArcConverter(
        myLastPoint, p,
        r.getVector(ELLIPTICAL_ARC_R),
        r.getParam(ELLIPTICAL_ARC_ANGLE),
        r.getParam(ELLIPTICAL_ARC_LARGE_ARC) != 0.0,
        r.getParam(ELLIPTICAL_ARC_SWEEP) != 0.0)

    while (converter.hasNextSegment()) {
      val params = converter.nextSegment()
      myContext.bezierCurveTo(
          params[0], params[1],
          params[2], params[3],
          params[4], params[5])
    }
    myLastPoint = p
  }

  private fun smoothCurveBasePoint(vector: DoubleVector): DoubleVector {
    return DoubleVector(2 * myLastPoint.x - vector.x, 2 * myLastPoint.y - vector.y)
  }

  companion object {
    private const val MOVE_TO_P = 0
    private const val LINE_TO_P = 0
    private const val HORIZONTAL_LINE_TO_X = 0
    private const val VERTICAL_LINE_TO_Y = 0
    private const val CURVE_TO_P1 = 0
    private const val CURVE_TO_P2 = 2
    private const val CURVE_TO_P = 4
    private const val SMOOTH_CURVE_TO_P1 = 0
    private const val SMOOTH_CURVE_TO_P = 2
    private const val QUADRATIC_BEZIER_CURVE_TO_P1 = 0
    private const val QUADRATIC_BEZIER_CURVE_TO_P = 2
    private const val SMOOTH_QUADRATIC_BEZIER_CURVE_TO_P = 0
    private const val ELLIPTICAL_ARC_R = 0
    private const val ELLIPTICAL_ARC_ANGLE = 2
    private const val ELLIPTICAL_ARC_LARGE_ARC = 3
    private const val ELLIPTICAL_ARC_SWEEP = 4
    private const val ELLIPTICAL_ARC_P = 5

    fun apply(path: String, ctx: Context2d) {
      val processor = PathProcessor(ctx)
      processor.applyPath(ParsingUtil.parsePath(path))
    }
  }
}

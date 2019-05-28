package jetbrains.datalore.visualization.base.svgToCanvas

import jetbrains.datalore.base.geometry.DoubleVector
import kotlin.math.*

internal class ArcConverter(private var myFrom: DoubleVector, to: DoubleVector, r: DoubleVector,
                            angle: Double?, largeArcFlag: Boolean, sweepFlag: Boolean) {
    private var myC: DoubleVector? = null

    private var mySegIndex = 0.0
    private var mySegNum = 0.0

    private var mySin: Double = 0.0
    private var myCos: Double = 0.0

    private var myRx: Double = 0.toDouble()
    private var myRy: Double = 0.toDouble()

    private var myTheta: Double = 0.toDouble()
    private var myDelta: Double = 0.0
    private var myT: Double = 0.0

    init {

        if (myFrom.x != to.x || myFrom.y != to.y) {
            // Convert to center parametrization as shown in
            // http://www.w3.org/TR/SVG/implnote.htmls
            myRx = abs(r.x)
            myRy = abs(r.y)

            mySin = sin(toRadians(angle!!))
            myCos = cos(toRadians(angle))

            val x1dash = myCos * (myFrom.x - to.x) / 2 + mySin * (myFrom.y - to.y) / 2
            val y1dash = -mySin * (myFrom.x - to.x) / 2 + myCos * (myFrom.y - to.y) / 2
            val numerator = myRx * myRx * myRy * myRy - myRx * myRx * y1dash * y1dash - myRy * myRy * x1dash * x1dash

            val root: Double
            //  If mRx , mRy and are such that there is no solution (basically,
            //  the ellipse is not big enough to reach from 'from' to 'to'
            //  then the ellipse is scaled up uniformly until there is
            //  exactly one solution (until the ellipse is just big enough).
            //  -> find factor s, such that numerator' with mRx'=s*mRx and mRy'=s*mRy becomes 0
            if (numerator < 0) {
                val s = sqrt(1 - numerator / (myRx * myRx * myRy * myRy))
                myRx *= s
                myRy *= s
                root = 0.0
            } else {
                root = (if (largeArcFlag == sweepFlag) -1 else 1) * sqrt(numerator / (myRx * myRx * y1dash * y1dash + myRy * myRy * x1dash * x1dash))
            }

            @Suppress("SpellCheckingInspection")
            val cxdash = root * myRx * y1dash / myRy
            @Suppress("SpellCheckingInspection")
            val cydash = -root * myRy * x1dash / myRx

            myC = DoubleVector(
                    myCos * cxdash - mySin * cydash + (myFrom.x + to.x) / 2,
                    mySin * cxdash + myCos * cydash + (myFrom.y + to.y) / 2)

            myTheta = calcVectorAngle(
                    1.0, 0.0,
                    (x1dash - cxdash) / myRx, (y1dash - cydash) / myRy)

            @Suppress("SpellCheckingInspection")
            var dtheta = calcVectorAngle(
                    (x1dash - cxdash) / myRx, (y1dash - cydash) / myRy,
                    (-x1dash - cxdash) / myRx, (-y1dash - cydash) / myRy)

            if (!sweepFlag && dtheta > 0) {
                dtheta -= 2 * PI
            } else if (sweepFlag && dtheta < 0) {
                dtheta += 2 * PI
            }

            // Convert into cubic bezier segments <= 90deg
            mySegNum = ceil(abs(dtheta / (PI / 2)))
            myDelta = dtheta / mySegNum
            myT = (8 / 3).toDouble() * sin(myDelta / 4) * sin(myDelta / 4) / sin(myDelta / 2)
        }
    }

    private fun calcVectorAngle(ux: Double, uy: Double, vx: Double, vy: Double): Double {
        val ta = atan2(uy, ux)
        val tb = atan2(vy, vx)
        return if (tb >= ta) {
            tb - ta
        } else 2 * PI - (ta - tb)
    }

    fun hasNextSegment(): Boolean {
        return mySegIndex < mySegNum
    }

    fun nextSegment(): List<Double> {
        if (!hasNextSegment()) {
            throw NoSuchElementException("There are no more segments.")
        }

        val cosTheta1 = cos(myTheta)
        val sinTheta1 = sin(myTheta)
        val theta2 = myTheta + myDelta
        val cosTheta2 = cos(theta2)
        val sinTheta2 = sin(theta2)

        val list = mutableListOf<Double>()
        // a) calculate endpoint of the segment:
        val to = DoubleVector(
                myCos * myRx * cosTheta2 - mySin * myRy * sinTheta2 + myC!!.x,
                mySin * myRx * cosTheta2 + myCos * myRy * sinTheta2 + myC!!.y)

        // b) calculate gradients at start/end points of segment:
        list.add(myFrom.x + myT * (-myCos * myRx * sinTheta1 - mySin * myRy * cosTheta1))
        list.add(myFrom.y + myT * (-mySin * myRx * sinTheta1 + myCos * myRy * cosTheta1))
        list.add(to.x + myT * (myCos * myRx * sinTheta2 + mySin * myRy * cosTheta2))
        list.add(to.y + myT * (mySin * myRx * sinTheta2 - myCos * myRy * cosTheta2))

        list.add(to.x)
        list.add(to.y)

        // do next segment
        myTheta = theta2
        myFrom = to
        mySegIndex++
        return list
    }
}

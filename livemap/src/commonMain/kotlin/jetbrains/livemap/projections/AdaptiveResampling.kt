package jetbrains.livemap.projections

import jetbrains.datalore.base.geometry.DoubleSegment
import jetbrains.datalore.base.geometry.DoubleVector

class AdaptiveResampling(private val transform: (DoubleVector) -> DoubleVector, epsilon: Double) {
    private val epsilonSqr: Double = epsilon * epsilon
    private fun getLast(points: List<DoubleVector>): DoubleVector {
        return points[points.size - 1]
    }

    private fun pop(points: MutableList<DoubleVector>): DoubleVector {
        val last = points[points.size - 1]
        points.removeAt(points.size - 1)
        return last
    }

    fun resample(points: List<DoubleVector>): List<DoubleVector> {
        val result = ArrayList<DoubleVector>(points.size)

        for (i in 1 until points.size) {
            val sample = resample(points[i - 1], points[i])

            if (!result.isEmpty()) {
                pop(result)
            }

            sample.forEach { p -> result.add(transform(p)) }
        }

        return result
    }

    fun resample(p1: DoubleVector, p2: DoubleVector): List<DoubleVector> {
        val result = ArrayList<DoubleVector>()
        val candidates = ArrayList<DoubleVector>()
        result.add(p1)
        candidates.add(p2)

        while (!candidates.isEmpty()) {
            val samplePoint = getSamplePoint(getLast(result), getLast(candidates))

            if (samplePoint == null) {
                result.add(pop(candidates))
            } else {
                candidates.add(samplePoint)
            }
        }
        return result
    }

    private fun getSamplePoint(p1: DoubleVector, p2: DoubleVector): DoubleVector? {
        val pc = DoubleVector((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
        val q1 = transform(p1)
        val q2 = transform(p2)
        val qc = transform(pc)

        val distance = if (q1 == q2) {
            DoubleSegment(q1, qc).length()
        } else {
            distance(qc, q1, q2)
        }

        return if (distance < epsilonSqr) null else pc
    }

    private fun distance(p: DoubleVector, l1: DoubleVector, l2: DoubleVector): Double {
        val ortX = l2.x - l1.x
        val ortY = -(l2.y - l1.y)

        val dot = (p.x - l1.x) * ortY + (p.y - l1.y) * ortX
        val len = ortY * ortY + ortX * ortX

        return dot * dot / len
    }
}
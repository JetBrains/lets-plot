/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble

import jetbrains.datalore.plot.base.PositionAdjustment
import jetbrains.datalore.plot.base.pos.PositionAdjustments
import jetbrains.datalore.plot.base.pos.StackingMode
import jetbrains.datalore.plot.builder.PosProviderContext
import kotlin.jvm.JvmOverloads

abstract class PosProvider {

    abstract fun createPos(ctx: PosProviderContext): PositionAdjustment

    abstract fun handlesGroups(): Boolean

    companion object {

        fun wrap(pos: PositionAdjustment): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    return pos
                }

                override fun handlesGroups(): Boolean {
                    return pos.handlesGroups()
                }
            }
        }

        fun barStack(vjust: Double? = null, stackingMode: StackingMode = StackingMode.ALL): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    return PositionAdjustments.stack(ctx.aesthetics, vjust, stackingMode)
                }

                override fun handlesGroups(): Boolean {
                    return PositionAdjustments.Meta.STACK.handlesGroups()
                }
            }
        }

        @JvmOverloads
        fun dodge(width: Double? = null): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    val aesthetics = ctx.aesthetics
                    val groupCount = ctx.groupCount
                    return PositionAdjustments.dodge(aesthetics, groupCount, width)
                }

                override fun handlesGroups(): Boolean {
                    return PositionAdjustments.Meta.DODGE.handlesGroups()
                }
            }
        }

        fun fill(vjust: Double? = null, stackingMode: StackingMode = StackingMode.ALL): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    return PositionAdjustments.fill(ctx.aesthetics, vjust, stackingMode)
                }

                override fun handlesGroups(): Boolean {
                    return PositionAdjustments.Meta.FILL.handlesGroups()
                }
            }
        }

        fun jitter(width: Double?, height: Double?): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    return PositionAdjustments.jitter(width, height)
                }

                override fun handlesGroups(): Boolean {
                    return PositionAdjustments.Meta.JITTER.handlesGroups()
                }
            }
        }

        fun nudge(width: Double?, height: Double?): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    return PositionAdjustments.nudge(width, height)
                }

                override fun handlesGroups(): Boolean {
                    return PositionAdjustments.Meta.NUDGE.handlesGroups()
                }
            }
        }

        /*
  public static PosProvider jitterDodge(Double jitterWidth, Double jitterHeight) {
    return jitterDodge(null, jitterWidth, jitterHeight);
  }
  */

        fun jitterDodge(width: Double?, jitterWidth: Double?, jitterHeight: Double?): PosProvider {
            return object : PosProvider() {
                override fun createPos(ctx: PosProviderContext): PositionAdjustment {
                    val aesthetics = ctx.aesthetics
                    val groupCount = ctx.groupCount
                    return PositionAdjustments.jitterDodge(aesthetics, groupCount, width, jitterWidth, jitterHeight)
                }

                override fun handlesGroups(): Boolean {
                    return PositionAdjustments.Meta.JITTER_DODGE.handlesGroups()
                }
            }
        }
    }
}

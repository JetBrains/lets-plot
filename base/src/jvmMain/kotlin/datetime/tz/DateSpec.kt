package jetbrains.datalore.base.datetime.tz

import jetbrains.datalore.base.datetime.Date

internal interface DateSpec {
    val rRule: String
    fun getDate(year: Int): Date
}

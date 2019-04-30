package jetbrains.datalore.base.numberFormat

expect object NumberFormatUtil {

    fun formatNumber(num: Number, pattern: String): String

    //  def decimal pattern    :#,##0.###
    //  def scientific pattern :#E0
    //  def currency pattern :¤#,##0.00;(¤#,##0.00)
    //  def percent pattern :#,##0%
    //  def decimal pattern    :7,500.25
    //  def scientific pattern :8E3
    //  def currency pattern :US$7,500.25
    //  def percent pattern :750,025%
}

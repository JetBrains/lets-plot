package jetbrains.livemap.demo

private const val DEMO_PROJECT = "livemap-demo"
private const val CALL_FUN = "jetbrains.livemap.demo.featuresDemo"
private val LIBS = BrowserDemoUtil.KOTLIN_LIBS + BrowserDemoUtil.BASE_MAPPER_LIBS + BrowserDemoUtil.PLOT_LIBS + BrowserDemoUtil.DEMO_COMMON_LIBS

fun main() {
    BrowserDemoUtil.openInBrowser(DEMO_PROJECT) {
        BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, LIBS, "Features Demo")
    }
}
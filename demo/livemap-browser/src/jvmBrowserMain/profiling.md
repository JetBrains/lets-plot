The best way to profile JS code is to use Chrome Canary. It shows real function names in the profiler when source map is available, even for minified code.

Alternatively, use a non-minified demo. To do so:
- build `js-package` module using Gradle task `js-package:jsBrowserDevelopmentWebpack`
- build `demo-livemap` module using Gradle task `demo-livemap:jsBrowserDevelopmentRun`

Open a `.kt` file with demo that you want to profile. Set the parameter `dev=true` for two functions:
- `BrowserDemoUtil.openInBrowser(dev = true)`
- `BrowserDemoUtil.mapperDemoHtml(DEMO_PROJECT, CALL_FUN, DEMO_TITLE, dev = true)`

Now you can run the demo and profile it in Chrome DevTools.
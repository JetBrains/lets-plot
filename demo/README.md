# How to run browser demo:
- build the js-package module  
    `./gradlew :js-package:build`
- build browser demos:  
    `./gradlew :demo-plot-browser:build`  
    `./gradlew :demo-livemap-browser:build`  
    `./gradlew :demo-svg-browser:build`
- Run any demo inside the `demoRunnerMain` 

# Seeing the changes from the core libs
After modifying code in the core libraries the `js-package` module must be rebuilt. Run the demo or simply reload the browser page to see the changes. Rebuilding the demo modules is not necessary.


## Troubleshooting:

### Error: `'PWD' env variable is not defined`
Add an environment variable to the run configuration. The path should point to the project root, e.g. `/Users/me/Projects/lets-plot`. You can retrieve the path by right-clicking the project root and selecting `Copy Path` -> `Absolute Path`.

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/demo/run_config.png" >

### Error: `Could not find or load main class ...<DemoName>Kt`
This error occurs when the `main()` function is placed inside a `companion object`. Move the main function outside the companion object or modify the run configuration by removing the `Kt` suffix from the main class name.

### Error: `No such file or directory` from BrowserDemoUtil.kt

The demo module is not built. Run:  
`./gradlew :demo-plot-browser:build`

### Error: `File not found: '../js-package/build/dist/js/productionExecutable/lets-plot.js'`

The js-package module is not built. Run:  
`./gradlew :js-package:build`


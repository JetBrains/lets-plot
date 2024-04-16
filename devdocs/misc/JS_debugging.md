How to debug `jvmBrowser` demos in browser:
- Run the task `lets-plot->Tasks-> kotlin browser->jsBrowserDevelopmentWebpack` to build dev version of the library and demos
- Select the demo you want to debug
- Add evn variable `DEV=1` to the run configuration
- Run the demo

After that, demo will be opened in the browser and you can debug it using browser's dev tools:
- Code should be non-minified
- Source maps should be available
- You can set breakpoints in the code
- You can see the real function and variable names

<br>  

Note about `Gradle runner`:
- PWD should be added manually to environment variables in the run configuration
 - In case of `Class not found` the `Kt` suffix should be removed from `-DmainClass` in the run configuration

Note about `livemap`:  
- The Sources tab in the dev tools `livemap` may be duplicated - in the top level `LetsPlot` and in the top level `demo-livemap.livemap`. Breakpoints will only work with the `demo-livemap.livemap` sources.


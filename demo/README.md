## Troubleshooting:

### Error: `'PWD' env variable is not defined`
Add an environment variable to the run configuration. The path should point to the project root, e.g. `/Users/me/Projects/lets-plot`. You can retrieve the path by right-clicking the project root and selecting `Copy Path` -> `Absolute Path`.

<img src="https://raw.githubusercontent.com/JetBrains/lets-plot/master/demo/run_config.png" >

### Error: `Could not find or load main class ...<DemoName>Kt`
This error occurs when the `main()` function is placed inside a `companion object`. Move the main function outside the companion object or modify the run configuration by removing the `Kt` suffix from the main class name.

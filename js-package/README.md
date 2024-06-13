# lets-plot JS library
                    
## Build artifacts

> **Note**: These commands must be run from the project root.
> If you want run them from this folder, remove `js-package:` from the task name.


 - `./gradlew js-package:jsBrowserProductionWebpack` - builds minified JS package `lets-plot.min.js` for embedding;
 - `./gradlew js-package:jsBrowserDevelopmentWebpack` - builds full-size JS package `lets-plot.js` for develop purpose;

> **Note**: For more details about debugging JS demos check [JS debugging.md](../devdocs/misc/JS_debugging.md).

After build artifacts can be found inside `build` directory: `js-package/build/distributions`.

- `./gradlew js-package:copyForPublish` - copies minified JS package for release.
                                   
Release artifacts are located inside `distr` directory: `js-package/distr`.


## CDN

Lets-Plot project uses `JsDelivr` as CDN service for JS artifacts:

https://www.jsdelivr.com

For CDN update release JS artifacts should be pushed to the project repository with `git tag` (check [RELEASE.md](https://github.com/JetBrains/lets-plot/blob/master/RELEASE.md#3-build-and-copy-javascript-artifacts-to-the-publish-directory)).

CDN link to release artifact looks like:

`https://github.com/JetBrains/lets-plot/releases/download/<RELEASE_VERSION>/lets-plot.min.js`

For example: https://github.com/JetBrains/lets-plot/releases/download/v3.2.0/lets-plot.min.js

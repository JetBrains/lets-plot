# lets-plot JS library.

## Building

* build project with Gradle (`./gradlew build`)

* take library files in `js-package/build/dist` directory


## Publishing in Bintray CDN

* set `bintray.user` and `bintray.key` in `build_settings.yml` with your Bintray credentials

* run `:js-package:bintrayUpload` gradle task (`./gradlew :js-package:bintrayUpload`) 
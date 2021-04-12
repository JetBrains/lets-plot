# lets-plot JS library.
                                                       
## `js-package/distr`

During the project release, Lets-Plot JS artifacts are copied to the `distr` folder
(see [RELEASE.md](https://github.com/JetBrains/lets-plot/blob/master/RELEASE.md#3-build-and-copy-javascript-artifacts-to-the-publish-directory)):

- lets-plot.js
- lets-plot.min.js

This GitHub location (https://github.com/JetBrains/lets-plot/tree/master/js-package/distr) is
set up for **git auto-update** at cdnjs.com.

See: [cdnjs/packages/l/lets-plot.json](https://github.com/cdnjs/packages/blob/master/packages/l/lets-plot.json)

Note: the file lets-plot.js is ignored by CDNJS due to its large size (45MB > 25MB) and 
not available via CDN.  

## CDN

See: https://cdnjs.com/libraries/lets-plot

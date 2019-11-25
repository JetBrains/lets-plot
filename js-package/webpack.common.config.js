/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

const path = require('path');
const webpack = require('webpack');

const buildPath = path.resolve(__dirname, 'build');
const libPath = path.resolve(buildPath, 'js');
const jsPackagePath = path.resolve(buildPath, 'classes', 'kotlin', 'js', 'main');
const distPath = path.resolve(buildPath, 'dist');

module.exports = {
    entry: `${jsPackagePath}/js-package.js`,
    output: {
        library: 'LetsPlot',
        path: distPath,
        libraryTarget: 'window',
        globalObject: 'window'
    },
    resolve: {
        modules: [libPath, jsPackagePath, 'node_modules']
    },
    plugins: [
        new webpack.IgnorePlugin(/^(ws|text-encoding)$/)
    ]
};
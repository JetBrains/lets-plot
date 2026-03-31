/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

const path = require('path');
const fs = require('fs');

const artifactsRoot = path.resolve(config.basePath, '../../../../wasmjs-package/build/reports');

config.files.push({
    pattern: path.resolve(__dirname, '../../../../wasmjs-package/src/wasmJsTest/resources/**/*'),
    watched: false,
    included: false,
    served: true,
    nocache: false
});

config.middleware = config.middleware || [];
config.middleware.push('visual-test-artifacts');

config.plugins = config.plugins || [];
config.plugins.push({
    'middleware:visual-test-artifacts': ['factory', function() {
        return function(req, res, next) {
            if (req.method !== 'POST' || req.url !== '/__visual_testing__/artifacts') {
                return next();
            }

            let body = '';
            req.on('data', (chunk) => {
                body += chunk;
            });

            req.on('end', () => {
                try {
                    const payload = JSON.parse(body);
                    const relativePath = payload.relativePath;
                    const base64 = payload.base64;
                    const outputPath = path.resolve(artifactsRoot, relativePath);

                    fs.mkdirSync(path.dirname(outputPath), { recursive: true });
                    fs.writeFileSync(outputPath, Buffer.from(base64, 'base64'));

                    res.writeHead(200, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ outputPath }));
                } catch (error) {
                    res.writeHead(500, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify({ error: String(error) }));
                }
            });
        };
    }]
});

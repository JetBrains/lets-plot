/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

(function () {
    const karma = window.__karma__;
    if (!karma) {
        return;
    }

    const originalLoaded = karma.loaded.bind(karma);
    const fonts = [
        { family: 'Noto Sans', weight: '400', style: 'normal', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSans-Regular.ttf', probe: '16px "Noto Sans"' },
        { family: 'Noto Sans', weight: '400', style: 'italic', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSans-Italic.ttf', probe: 'italic 16px "Noto Sans"' },
        { family: 'Noto Sans', weight: '700', style: 'normal', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSans-Bold.ttf', probe: 'bold 16px "Noto Sans"' },
        { family: 'Noto Sans', weight: '700', style: 'italic', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSans-BoldItalic.ttf', probe: 'italic bold 16px "Noto Sans"' },
        { family: 'Noto Sans Mono', weight: '400', style: 'normal', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSansMono-Regular.ttf', probe: '16px "Noto Sans Mono"' },
        { family: 'Noto Sans Mono', weight: '700', style: 'normal', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSansMono-Bold.ttf', probe: 'bold 16px "Noto Sans Mono"' },
        { family: 'Noto Serif', weight: '400', style: 'normal', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSerif-Regular.ttf', probe: '16px "Noto Serif"' },
        { family: 'Noto Serif', weight: '400', style: 'italic', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSerif-Italic.ttf', probe: 'italic 16px "Noto Serif"' },
        { family: 'Noto Serif', weight: '700', style: 'normal', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSerif-Bold.ttf', probe: 'bold 16px "Noto Serif"' },
        { family: 'Noto Serif', weight: '700', style: 'italic', relativePath: 'wasmjs-package/src/wasmJsTest/resources/fonts/NotoSerif-BoldItalic.ttf', probe: 'italic bold 16px "Noto Serif"' }
    ];

    function findFontUrl(relativePath) {
        const files = Object.keys(karma.files || {});
        return files.find((path) => path.endsWith('/' + relativePath) || path.endsWith(relativePath))
            || ('/base/' + relativePath);
    }

    function installFontFaces() {
        const style = document.createElement('style');
        style.textContent = fonts.map((font) => `
            @font-face {
                font-family: "${font.family}";
                src: url("${findFontUrl(font.relativePath)}") format("truetype");
                font-weight: ${font.weight};
                font-style: ${font.style};
            }
        `).join('\n');
        document.head.appendChild(style);
    }

    function preloadFonts() {
        installFontFaces();
        return Promise.all(fonts.map((font) => document.fonts.load(font.probe)))
            .then(() => document.fonts.ready);
    }

    karma.loaded = function () {
        preloadFonts()
            .catch((error) => {
                console.error('Failed to preload wasmjs test fonts.', error);
            })
            .finally(() => {
                originalLoaded();
            });
    };
})();

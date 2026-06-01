/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

config.browserDisconnectTimeout = 60000;
config.browserDisconnectTolerance = 3;
config.browserNoActivityTimeout = 600000;
config.captureTimeout = 120000;
config.pingTimeout = 60000;
config.client = config.client || {};
config.client.mocha = config.client.mocha || {};
config.client.mocha.timeout = 600000;
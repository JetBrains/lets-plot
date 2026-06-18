/*
  Copyright (c) 2025. JetBrains s.r.o.
  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
*/

/*
  Compatibility stubs for obsolete POSIX crypt functions.

  Kotlin/Native's `platform.posix` bindings reference the DES routines
  `encrypt` and `setkey`. These were part of glibc's libcrypt, but libxcrypt
  4.x (the libcrypt.so.2 shipped by modern distros and conda) dropped the
  obsolete DES API, keeping only `crypt`. Because CPython loads extension
  modules with RTLD_NOW, every undefined symbol must resolve at import time,
  so these dead references make the bridge fail to load with
  'undefined symbol: encrypt'.

  lets-plot never calls `encrypt`/`setkey`, so providing inert definitions here
  satisfies the linker without relying on an obsolete-API libcrypt being present
  on the build host or the end user's machine. Linux-only: on macOS these live
  in libSystem and on Windows they are not referenced, so defining our own there
  would risk a duplicate-symbol clash (see setup.py, which compiles this only on
  Linux).
*/

void encrypt(char block[64], int edflag) {
    (void) block;
    (void) edflag;
}

void setkey(const char *key) {
    (void) key;
}

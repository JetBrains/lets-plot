#!/bin/bash

#
# Copyright (c) 2025. JetBrains s.r.o.
# Use of this source code is governed by the MIT license that can be found in the LICENSE file.
#

# Get the IPython startup directory
IPYTHON_STARTUP_DIR="$HOME/.ipython/profile_default/startup"

# Ensure the directory exists
mkdir -p "$IPYTHON_STARTUP_DIR"

# Define the startup script name
STARTUP_SCRIPT="50-lets-plot-hook.py"

# Ensure the script exists in the current directory
if [[ ! -f "$STARTUP_SCRIPT" ]]; then
    echo "❌ Error: File '$STARTUP_SCRIPT' not found in the current directory."
    exit 1
fi

# Copy the script to the IPython startup directory
cp "$STARTUP_SCRIPT" "$IPYTHON_STARTUP_DIR/"

# Confirm the installation
echo "✅ '$STARTUP_SCRIPT' has been installed in: $IPYTHON_STARTUP_DIR"
echo "🔄 Restart JupyterLab or Jupyter Notebook to apply changes."
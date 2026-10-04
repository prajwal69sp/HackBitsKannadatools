#!/usr/bin/env bash
set -eu

SDK_ROOT="${ANDROID_SDK_ROOT:-${HOME}/android-sdk}"
TOOLS_ROOT="${SDK_ROOT}/cmdline-tools/latest"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "$TMP_DIR"' EXIT

mkdir -p "${SDK_ROOT}/cmdline-tools"
curl -fsSL "https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip" -o "${TMP_DIR}/commandline-tools.zip"
unzip -q "${TMP_DIR}/commandline-tools.zip" -d "${TMP_DIR}"
rm -rf "${TOOLS_ROOT}"
mkdir -p "${TOOLS_ROOT}"
mv "${TMP_DIR}/cmdline-tools/"* "${TOOLS_ROOT}/"

export ANDROID_HOME="${SDK_ROOT}"
export ANDROID_SDK_ROOT="${SDK_ROOT}"
yes | "${TOOLS_ROOT}/bin/sdkmanager" --sdk_root="${SDK_ROOT}" --licenses >/dev/null
"${TOOLS_ROOT}/bin/sdkmanager" --sdk_root="${SDK_ROOT}" \
  "platform-tools" \
  "platforms;android-36" \
  "build-tools;36.0.0"

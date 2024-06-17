# AssetSideLoader

## Introduction

A LSPosed module that replaces resources in External Storage or APK by hooking UnityEngine's internal methods to load files from a custom path.

---
## Description

- You need to grant "Get App List" permission manually.
- Please activate the module in LSPosed and select at least one target app, then launch the app.
- After launching the app, you can select the target app in `SELECT APPS`, then click `DONE` to return.
  - Note that even if you have selected a target app in the module, you still need to select the corresponding target app for the module in LSPosed.
  - The `SELECT APPS` will only show applications that have `libil2cpp.so` in the `lib` directory.
- After selecting the target application, you can click the corresponding application in the main interface to enter the specific settings.
  - If the corresponding app is not displayed after returning to the main interface, please restart the app.
- There are three paths you need to fill in manually
  - `APK Patch`: the path of resources in APK file relative to `/data/app/*/*/base.apk!/`.
  - `Data Patch`: path to resources in external storage relative to `/storage/emulated/0/Android/com.example.www/files`.
  - `Mod Patch`: the path to the customized mod folder, which is a relative path in the external storage like the Data Patch.
  - APP expects the same directory structure and file names under `Mod Patch` as the files to be replaced in `APK Patch` and `Data Patch`.
- After selecting the path, click `SAVE` to save the settings, click `DELETE` to delete the settings, and click SWITCH to enable or disable the settings.
- After making sure the settings are correct, start the target application.

## Example

![example](doc/example.jpg)

## Credits

- Perfare: [Zygisk-Il2CppDumper](https://github.com/Perfare/Zygisk-Il2CppDumper)
- jmpewsL [Dobby](https://github.com/jmpews/Dobby)
- LSPosed: [LSPosed](https://github.com/LSPosed/LSPosed)
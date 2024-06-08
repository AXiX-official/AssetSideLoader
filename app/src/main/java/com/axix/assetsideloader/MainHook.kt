package com.axix.assetsideloader

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.bilibili.azurlane") {
            Log.i("IL2CPP_Bridge", "Loaded package: ${lpparam.packageName}")
            AssetSideLoader().InitHook()
        }
    }
}
package com.axix.assetsideloader

import android.util.Log
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.bilibili.azurlane") {
            Log.i("AssetSideLoader", "Target application: ${lpparam.packageName} launched")
            AssetSideLoader().InitHook("com.bilibili.azurlane")
        }
    }
}
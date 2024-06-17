package top.axix.assetsideloader

import android.util.Log
import com.google.gson.Gson
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import top.axix.assetsideloader.MainActivity.Companion.walkDirectoryTree
import java.io.File

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (!lpparam.isFirstApplication){
            return
        }

        val libDir = File(lpparam.appInfo.nativeLibraryDir)
        if (libDir.walkDirectoryTree().any { it.name == "libil2cpp.so" }) {
            var prefs = getPref("AssetSideLoader")
            prefs?.let {
                val appInfoJsonStr: String? = prefs!!.getString("AppInfos", null)
                appInfoJsonStr?.let {
                    val gson = Gson()
                    val appInfoList =
                        gson.fromJson(appInfoJsonStr, Array<AppInfo>::class.java).toList()
                    val appInfo = appInfoList.find { it.packageName == lpparam.packageName }

                    if (appInfo != null && appInfo.isSelected && appInfo.isEnabled) {
                        if (appInfo.packageName == getProcessName(lpparam.classLoader)){
                            Log.i(
                                "AssetSideLoader",
                                "Target application: ${lpparam.packageName} launched"
                            )
                            AssetSideLoader().InitHook(
                                appInfo.packageName,
                                GetPath(appInfo.dataPath, appInfo.dataPatch),
                                GetPath(appInfo.apkPath, appInfo.apkPatch, true),
                                GetPath(appInfo.dataPath, appInfo.modPatch)
                            )
                        }
                    }
                }
            } ?: Log.e("AssetSideLoader", "Cannot load pref for A properly")
        }
    }

    private fun getProcessName(classLoader: ClassLoader): String? {
        try {
            val activityThread = Class.forName("android.app.ActivityThread", false, classLoader)
            val currentActivityThread = activityThread.getMethod("currentActivityThread").invoke(null)
            val getProcessName = activityThread.getMethod("getProcessName")
            return getProcessName.invoke(currentActivityThread) as? String
        } catch (e: Throwable) {
            XposedBridge.log("Error getting process name: ${e.message}")
            return null
        }
    }

    companion object {
        fun getPref(path: String): XSharedPreferences? {
            val pref = XSharedPreferences("top.axix.assetsideloader", path)
            return if (pref.file.canRead()) pref else null
        }

        fun GetPath(prefix: String, path: String, isJar: Boolean = false): String {
            var p: String
            if (isJar){
                p = "jar:file://${prefix}" + if (path.startsWith("/")) path.substring(1) else path
            }
            else{
                p = prefix + if (path.startsWith("/")) path else "/${path}"
            }

            if (p.endsWith("/")) {
                p = p.substring(0, p.length - 1)
            }
            return p;
        }
    }
}
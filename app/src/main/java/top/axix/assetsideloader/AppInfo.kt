package top.axix.assetsideloader

import android.content.Context
import android.content.pm.ApplicationInfo
import com.google.gson.Gson
import java.io.Serializable

data class AppInfo(
    val appName: String,
    val packageName: String,
    val apkPath: String,
    var dataPath: String,
    var apkPatch: String,
    var dataPatch: String,
    var modPatch: String,
    var isEnabled: Boolean,
    var isSelected: Boolean
) : Serializable {
    constructor(context: Context, appInfo: ApplicationInfo) : this(
        appName = appInfo.loadLabel(context.packageManager).toString(),
        packageName = appInfo.packageName,
        apkPath = appInfo.sourceDir + "!/",
        dataPath = "/storage/emulated/0/Android/data/" + appInfo.packageName + "/files",
        apkPatch = "",
        dataPatch = "",
        modPatch = "",
        isEnabled = false,
        isSelected = false
    )

    fun toJson(): String {
        return Gson().toJson(this)
    }

    companion object {
        fun fromJson(json: String): AppInfo {
            return Gson().fromJson(json, AppInfo::class.java)
        }
    }
}

fun applicationInfoToAppInfo(context: Context, appInfos: List<ApplicationInfo>): List<AppInfo> {
    return appInfos.map { AppInfo(context, it) }
}
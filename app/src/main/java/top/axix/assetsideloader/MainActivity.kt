package top.axix.assetsideloader

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import top.axix.assetsideloader.Global.appInfoData
import java.io.File


object Global {
    var appInfoData: List<AppInfo> = emptyList()
}

class MainActivity : AppCompatActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = try {
            this.getSharedPreferences("AssetSideLoader", Context.MODE_WORLD_READABLE)
        } catch (e: SecurityException) {
            Log.e("AssetSideLoader", "Failed to get SharedPreferences: $e")
            null // other fallback, if any
        }

        sharedPreferences?.let {
        } ?: run {
            Toast(this).apply {
                setText("Failed to get SharedPreferences.")
                duration = Toast.LENGTH_SHORT
                show()
            }
        }

        appInfoData = ReadAppInfos()

        val openAppsButton = findViewById<Button>(R.id.select_apps_button)
        openAppsButton.setOnClickListener {
            val newInfo = getIl2cppApps()
            for (info in newInfo){
                if (appInfoData.none { it.packageName == info.packageName }) {
                    appInfoData = appInfoData + info
                }
            }
            val intent = Intent(this, AppsListActivity::class.java)
            startActivityForResult(intent, 1)
        }

        recyclerView = findViewById<RecyclerView>(R.id.apps_recycler_view)
        recyclerView.adapter = SelectedAppsAdapter(appInfoData)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        WriteAppInfos(appInfoData)
        (recyclerView.adapter as SelectedAppsAdapter).updateApps()
    }

    override fun onPause() {
        super.onPause()
        WriteAppInfos(appInfoData)
    }

    private fun ReadAppInfos(): List<AppInfo> {
        val appInfoJsonStr = sharedPreferences?.getString("AppInfos", null)

        return if (appInfoJsonStr != null) {
            val gson = Gson()
            gson.fromJson(appInfoJsonStr, Array<AppInfo>::class.java).toList()
        } else {
            getIl2cppApps()
        }
    }


    private fun WriteAppInfos(target: List<AppInfo>) {
        sharedPreferences?.let { prefs ->
            val gson = Gson()
            val appInfoJsonStr = gson.toJson(target)
            val editor = prefs.edit()
            editor.putString("AppInfos", appInfoJsonStr).apply()
        } ?: run {
            Log.e("AssetSideLoader", "SharedPreferences is null")
        }
    }


    companion object{
        fun File.walkDirectoryTree(): Sequence<File> = sequence {
            yield(this@walkDirectoryTree)
            if (this@walkDirectoryTree.isDirectory) {
                this@walkDirectoryTree.listFiles()?.forEach {
                    yieldAll(it.walkDirectoryTree())
                }
            }
        }
    }

    private fun getIl2cppApps() : List<AppInfo> {
        val userApps = this.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.sourceDir.startsWith("/data/app") }
        val apps = userApps
            .filter { appInfo ->
                val libDir = File(appInfo.nativeLibraryDir)
                libDir.walkDirectoryTree().any { it.name == "libil2cpp.so" }
            }
        return applicationInfoToAppInfo(this, apps)
    }
}
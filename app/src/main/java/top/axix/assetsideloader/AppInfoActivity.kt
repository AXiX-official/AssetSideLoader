package top.axix.assetsideloader

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import top.axix.assetsideloader.Global.appInfoData

class AppInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)

        val position = intent.getIntExtra("position", -1)

        if (position == -1){
            finish()
        }

        val appName = findViewById<TextView>(R.id.app_name)
        val appPackageName = findViewById<TextView>(R.id.app_pakage_name)
        val apkPath = findViewById<TextView>(R.id.apk_path)
        val apkPatch = findViewById<EditText>(R.id.apk_patch)
        var dataPath = findViewById<TextView>(R.id.data_path)
        var dataPatch = findViewById<EditText>(R.id.data_patch)
        var modPatch = findViewById<EditText>(R.id.mod_patch)
        val enableSwitch = findViewById<Switch>(R.id.enable_switch)
        val saveButton = findViewById<Button>(R.id.save_button)
        val deleteButton = findViewById<Button>(R.id.delete_button)

        appName.text = appInfoData[position].appName
        appPackageName.text = appInfoData[position].packageName
        apkPath.text = appInfoData[position].apkPath
        if (appInfoData[position].apkPatch != ""){
            apkPatch.setText(appInfoData[position].apkPatch)
        }
        dataPath.text = appInfoData[position].dataPath
        if (appInfoData[position].dataPatch != ""){
            dataPatch.setText(appInfoData[position].dataPatch)
        }
        if (appInfoData[position].modPatch != ""){
            modPatch.setText(appInfoData[position].modPatch)
        }
        enableSwitch.isChecked = appInfoData[position].isEnabled
        enableSwitch.text = if (appInfoData[position].isEnabled) "Enabled" else "Disabled"
        enableSwitch.setOnCheckedChangeListener { _, isChecked ->
            appInfoData[position].isEnabled = isChecked
            enableSwitch.text = if (isChecked) "Enabled" else "Disabled"
        }

        saveButton.setOnClickListener {
            appInfoData[position].apkPatch = apkPatch.text.toString()
            appInfoData[position].dataPatch = dataPatch.text.toString()
            appInfoData[position].modPatch = modPatch.text.toString()
            setResult(RESULT_OK, intent)
            finish()
        }

        deleteButton.setOnClickListener {
            appInfoData[position].isSelected = false
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}
package top.axix.assetsideloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppsAdapter(private val apps: List<AppInfo>) : RecyclerView.Adapter<AppsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val appName: TextView = view.findViewById(R.id.app_name)
        val appPackageName: TextView = view.findViewById(R.id.app_package_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = apps[position]
        holder.appName.text = app.appName
        holder.appPackageName.text = app.packageName
        // 先移除旧的监听器，防止触发无效的回调
        holder.checkBox.setOnCheckedChangeListener(null)

        holder.checkBox.isChecked = app.isSelected

        // 设置新的监听器
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            app.isSelected = isChecked
        }
    }

    override fun getItemCount() = apps.size
}